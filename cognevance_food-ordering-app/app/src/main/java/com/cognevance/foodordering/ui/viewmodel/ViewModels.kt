package com.cognevance.foodordering.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognevance.foodordering.data.local.CartItemEntity
import com.cognevance.foodordering.data.model.FoodItem
import com.cognevance.foodordering.data.repository.AuthRepository
import com.cognevance.foodordering.data.repository.CartRepository
import com.cognevance.foodordering.data.repository.FoodRepository
import com.cognevance.foodordering.data.repository.OrderRepository
import com.cognevance.foodordering.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class FoodUiState(
    val items: List<FoodItem> = emptyList(),
    val filteredItems: List<FoodItem> = emptyList(),
    val categories: List<String> = listOf("All"),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class PaymentUiState(
    val cardNumber: String = "",
    val cardHolder: String = "",
    val expiry: String = "",
    val cvv: String = "",
    val paymentMethod: String = "Card",
    val isProcessing: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isLoggedIn get() = authRepository.isLoggedIn

    fun updateEmail(v: String) { _uiState.value = _uiState.value.copy(email = v, emailError = null) }
    fun updatePassword(v: String) { _uiState.value = _uiState.value.copy(password = v, passwordError = null) }
    fun updateName(v: String) { _uiState.value = _uiState.value.copy(name = v, nameError = null) }

    private fun validateLogin(): Boolean {
        val s = _uiState.value
        var valid = true
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.email).matches()) {
            _uiState.value = s.copy(emailError = "Invalid email address")
            valid = false
        }
        if (s.password.length < 6) {
            _uiState.value = _uiState.value.copy(passwordError = "Password must be at least 6 characters")
            valid = false
        }
        return valid
    }

    private fun validateSignup(): Boolean {
        val s = _uiState.value
        if (s.name.isBlank()) {
            _uiState.value = s.copy(nameError = "Name is required")
            return false
        }
        return validateLogin()
    }

    fun login(onSuccess: () -> Unit) {
        if (!validateLogin()) return
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.value = s.copy(isLoading = true, errorMessage = null)
            authRepository.login(s.email, s.password)
                .onSuccess { onSuccess() }
                .onFailure { _uiState.value = _uiState.value.copy(errorMessage = it.message) }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun signup(onSuccess: () -> Unit) {
        if (!validateSignup()) return
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.value = s.copy(isLoading = true, errorMessage = null)
            authRepository.signup(s.email, s.password, s.name)
                .onSuccess {
                    authRepository.currentUser?.let { user ->
                        userRepository.saveProfile(user.uid, s.email, s.name)
                    }
                    onSuccess()
                }
                .onFailure { _uiState.value = _uiState.value.copy(errorMessage = it.message) }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun logout() = authRepository.logout()

    class Factory(
        private val authRepository: AuthRepository,
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AuthViewModel(authRepository, userRepository) as T
    }
}

class FoodViewModel(private val foodRepository: FoodRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FoodUiState(isLoading = true))
    val uiState: StateFlow<FoodUiState> = _uiState.asStateFlow()

    init { loadFoods() }

    fun loadFoods() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            foodRepository.getFoodItems()
                .onSuccess { items ->
                    _uiState.value = _uiState.value.copy(
                        items = items,
                        categories = foodRepository.getCategories(items),
                        isLoading = false
                    )
                    applyFilters()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                }
        }
    }

    fun updateSearch(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyFilters()
    }

    private fun applyFilters() {
        val s = _uiState.value
        _uiState.value = s.copy(
            filteredItems = foodRepository.filterItems(s.items, s.searchQuery, s.selectedCategory)
        )
    }

    class Factory(private val foodRepository: FoodRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FoodViewModel(foodRepository) as T
    }
}

class CartViewModel(private val cartRepository: CartRepository) : ViewModel() {
    val cartItems = cartRepository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val cartCount = cartRepository.cartCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addToCart(food: FoodItem) {
        viewModelScope.launch { cartRepository.addToCart(food) }
    }

    fun updateQuantity(foodId: Int, quantity: Int) {
        viewModelScope.launch { cartRepository.updateQuantity(foodId, quantity) }
    }

    fun removeItem(foodId: Int) {
        viewModelScope.launch { cartRepository.removeItem(foodId) }
    }

    val cartTotal: StateFlow<Double> = cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        .let { flow ->
            MutableStateFlow(0.0).also { total ->
                viewModelScope.launch {
                    cartRepository.cartItems.collect { items ->
                        total.value = items.sumOf { it.price * it.quantity }
                    }
                }
            }
        }

    class Factory(private val cartRepository: CartRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CartViewModel(cartRepository) as T
    }
}

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val userId get() = authRepository.currentUser?.uid ?: ""

    val orders = orderRepository.getOrders(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _paymentState = MutableStateFlow(PaymentUiState())
    val paymentState: StateFlow<PaymentUiState> = _paymentState.asStateFlow()

    fun updateCardNumber(v: String) {
        _paymentState.value = _paymentState.value.copy(cardNumber = v.filter { it.isDigit() }.take(16))
    }

    fun updateCardHolder(v: String) {
        _paymentState.value = _paymentState.value.copy(cardHolder = v)
    }

    fun updateExpiry(v: String) {
        _paymentState.value = _paymentState.value.copy(expiry = v)
    }

    fun updateCvv(v: String) {
        _paymentState.value = _paymentState.value.copy(cvv = v.filter { it.isDigit() }.take(3))
    }

    fun setPaymentMethod(method: String) {
        _paymentState.value = _paymentState.value.copy(paymentMethod = method)
    }

    fun processPayment(cartItems: List<CartItemEntity>, onSuccess: () -> Unit) {
        val s = _paymentState.value
        if (s.paymentMethod == "Card") {
            if (s.cardNumber.length != 16) {
                _paymentState.value = s.copy(error = "Invalid card number")
                return
            }
            if (s.cardHolder.isBlank()) {
                _paymentState.value = s.copy(error = "Card holder name required")
                return
            }
            if (s.cvv.length != 3) {
                _paymentState.value = s.copy(error = "Invalid CVV")
                return
            }
        }

        viewModelScope.launch {
            _paymentState.value = s.copy(isProcessing = true, error = null)
            kotlinx.coroutines.delay(1500)
            orderRepository.placeOrder(userId, cartItems, s.paymentMethod)
                .onSuccess {
                    _paymentState.value = _paymentState.value.copy(isProcessing = false, isSuccess = true)
                    onSuccess()
                }
                .onFailure {
                    _paymentState.value = _paymentState.value.copy(
                        isProcessing = false,
                        error = it.message ?: "Payment failed"
                    )
                }
        }
    }

    class Factory(
        private val orderRepository: OrderRepository,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            OrderViewModel(orderRepository, authRepository) as T
    }
}
