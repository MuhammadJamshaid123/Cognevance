package com.cognevance.ecommerce.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cognevance.ecommerce.data.repository.AnalyticsRepository
import com.cognevance.ecommerce.data.repository.AuthRepository
import com.cognevance.ecommerce.data.repository.CustomerRepository
import com.cognevance.ecommerce.data.repository.OrderRepository
import com.cognevance.ecommerce.data.repository.ProductRepository
import com.cognevance.ecommerce.domain.model.AnalyticsData
import com.cognevance.ecommerce.domain.model.Customer
import com.cognevance.ecommerce.domain.model.Order
import com.cognevance.ecommerce.domain.model.OrderStatus
import com.cognevance.ecommerce.domain.model.Product
import com.cognevance.ecommerce.domain.model.User
import com.cognevance.ecommerce.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(
    private val authRepository: AuthRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = authRepository.observeCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val products = productRepository.observeProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders = orderRepository.observeAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val analytics = analyticsRepository.observeAnalytics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    private val _authState = MutableStateFlow(AuthUiState())
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        orderRepository.startRealtimeSync { }
        syncProducts()
    }

    override fun onCleared() {
        super.onCleared()
        orderRepository.stopRealtimeSync()
    }

    fun updateEmail(v: String) { _authState.value = _authState.value.copy(email = v) }
    fun updatePassword(v: String) { _authState.value = _authState.value.copy(password = v) }
    fun updateName(v: String) { _authState.value = _authState.value.copy(name = v) }

    fun login(onSuccess: () -> Unit) {
        val s = _authState.value
        viewModelScope.launch {
            _authState.value = s.copy(isLoading = true)
            authRepository.login(s.email, s.password)
                .onSuccess { onSuccess() }
                .onFailure { _message.value = it.message }
            _authState.value = _authState.value.copy(isLoading = false)
        }
    }

    fun register(onSuccess: () -> Unit) {
        val s = _authState.value
        viewModelScope.launch {
            _authState.value = s.copy(isLoading = true)
            authRepository.register(s.email, s.password, s.name, UserRole.CUSTOMER)
                .onSuccess { onSuccess() }
                .onFailure { _message.value = it.message }
            _authState.value = _authState.value.copy(isLoading = false)
        }
    }

    fun logout() = authRepository.logout()

    fun canAccess(required: UserRole): Boolean {
        val role = currentUser.value?.role ?: return false
        return authRepository.hasPermission(role, required)
    }

    fun syncProducts() {
        viewModelScope.launch {
            productRepository.syncProducts()
                .onSuccess { _message.value = "Synced $it products" }
                .onFailure { _message.value = "Sync failed: ${it.message}" }
        }
    }

    fun placeOrder(product: Product) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val order = Order(
                id = UUID.randomUUID().toString(),
                customerId = user.uid,
                customerName = user.name,
                productTitle = product.title,
                quantity = 1,
                totalAmount = product.price,
                status = OrderStatus.PENDING
            )
            orderRepository.placeOrder(order)
                .onSuccess { _message.value = "Order placed successfully" }
                .onFailure { _message.value = it.message }
        }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, status)
            _message.value = "Order updated to $status"
        }
    }

    fun loadCustomers() {
        if (!canAccess(UserRole.MANAGER)) return
        viewModelScope.launch {
            customerRepository.getCustomers()
                .onSuccess { _customers.value = it }
                .onFailure { _message.value = it.message }
        }
    }

    fun refreshAnalytics() {
        if (!canAccess(UserRole.MANAGER)) return
        viewModelScope.launch {
            analyticsRepository.refreshAnalytics(_customers.value.size.coerceAtLeast(1))
        }
    }

    fun clearMessage() { _message.value = null }

    class Factory(
        private val authRepository: AuthRepository,
        private val productRepository: ProductRepository,
        private val orderRepository: OrderRepository,
        private val customerRepository: CustomerRepository,
        private val analyticsRepository: AnalyticsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MainViewModel(authRepository, productRepository, orderRepository, customerRepository, analyticsRepository) as T
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val isLoading: Boolean = false
)
