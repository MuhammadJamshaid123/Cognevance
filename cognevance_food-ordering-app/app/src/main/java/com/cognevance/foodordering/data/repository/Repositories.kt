package com.cognevance.foodordering.data.repository

import android.content.Context
import com.cognevance.foodordering.data.local.CartDao
import com.cognevance.foodordering.data.local.CartItemEntity
import com.cognevance.foodordering.data.local.OrderDao
import com.cognevance.foodordering.data.local.OrderEntity
import com.cognevance.foodordering.data.local.UserProfileDao
import com.cognevance.foodordering.data.local.UserProfileEntity
import com.cognevance.foodordering.data.model.FoodItem
import com.cognevance.foodordering.data.model.OrderItemRequest
import com.cognevance.foodordering.data.model.OrderRequest
import com.cognevance.foodordering.data.remote.ApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FoodRepository(private val context: Context) {
    private val localFoods: List<FoodItem> by lazy {
        val json = context.assets.open("foods.json").bufferedReader().use { it.readText() }
        ApiClient.parseJson(json)
    }

    suspend fun getFoodItems(): Result<List<FoodItem>> {
        return try {
            Result.success(localFoods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCategories(items: List<FoodItem>): List<String> {
        return listOf("All") + items.map { it.category }.distinct().sorted()
    }

    fun filterItems(items: List<FoodItem>, query: String, category: String): List<FoodItem> {
        return items.filter { item ->
            val matchesQuery = query.isBlank() ||
                item.name.contains(query, ignoreCase = true) ||
                item.description.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || item.category == category
            matchesQuery && matchesCategory
        }
    }
}

class CartRepository(private val cartDao: CartDao) {
    val cartItems: Flow<List<CartItemEntity>> = cartDao.getCartItems()
    val cartCount: Flow<Int> = cartDao.getCartCount()

    suspend fun addToCart(food: FoodItem) {
        val existing = cartDao.getCartItems()
        cartDao.insert(
            CartItemEntity(
                foodId = food.id,
                name = food.name,
                price = food.price,
                imageUrl = food.imageUrl,
                quantity = 1
            )
        )
    }

    suspend fun updateQuantity(foodId: Int, quantity: Int) {
        if (quantity <= 0) cartDao.delete(foodId) else cartDao.updateQuantity(foodId, quantity)
    }

    suspend fun removeItem(foodId: Int) = cartDao.delete(foodId)
    suspend fun clearCart() = cartDao.clearCart()
}

class OrderRepository(
    private val orderDao: OrderDao,
    private val cartDao: CartDao
) {
    private val firestore = FirebaseFirestore.getInstance()

    fun getOrders(userId: String): Flow<List<OrderEntity>> = orderDao.getOrdersForUser(userId)

    suspend fun placeOrder(
        userId: String,
        cartItems: List<CartItemEntity>,
        paymentMethod: String
    ): Result<Long> {
        return try {
            val total = cartItems.sumOf { it.price * it.quantity }
            val itemsJson = Gson().toJson(cartItems)
            val orderEntity = OrderEntity(
                userId = userId,
                itemsJson = itemsJson,
                totalAmount = total,
                paymentMethod = paymentMethod,
                status = "confirmed"
            )
            val orderId = orderDao.insertOrder(orderEntity)

            val orderRequest = OrderRequest(
                userId = userId,
                items = cartItems.map {
                    OrderItemRequest(it.foodId, it.name, it.quantity, it.price)
                },
                totalAmount = total,
                paymentMethod = paymentMethod,
                status = "confirmed"
            )

            firestore.collection("orders")
                .add(orderRequest)
                .await()

            cartDao.clearCart()
            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    val currentUser get() = auth.currentUser
    val isLoggedIn get() = auth.currentUser != null

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(email: String, password: String, name: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.updateProfile(
                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            )?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() = auth.signOut()
}

class UserRepository(private val userProfileDao: UserProfileDao) {
    suspend fun saveProfile(userId: String, email: String, name: String) {
        userProfileDao.saveProfile(
            UserProfileEntity(userId = userId, email = email, displayName = name)
        )
    }

    suspend fun getProfile(userId: String) = userProfileDao.getProfile(userId)

    suspend fun updateProfile(profile: UserProfileEntity) = userProfileDao.updateProfile(profile)
}
