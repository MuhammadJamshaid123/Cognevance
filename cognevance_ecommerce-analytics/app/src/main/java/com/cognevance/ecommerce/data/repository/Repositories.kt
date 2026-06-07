package com.cognevance.ecommerce.data.repository

import com.cognevance.ecommerce.data.local.AnalyticsDao
import com.cognevance.ecommerce.data.local.AnalyticsEntity
import com.cognevance.ecommerce.data.local.OrderDao
import com.cognevance.ecommerce.data.local.OrderEntity
import com.cognevance.ecommerce.data.local.ProductDao
import com.cognevance.ecommerce.data.local.ProductEntity
import com.cognevance.ecommerce.data.remote.ApiClient
import com.cognevance.ecommerce.data.remote.toDomain
import com.cognevance.ecommerce.domain.model.AnalyticsData
import com.cognevance.ecommerce.domain.model.Customer
import com.cognevance.ecommerce.domain.model.Order
import com.cognevance.ecommerce.domain.model.OrderStatus
import com.cognevance.ecommerce.domain.model.Product
import com.cognevance.ecommerce.domain.model.User
import com.cognevance.ecommerce.domain.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val currentUser get() = auth.currentUser

    fun observeCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val fbUser = firebaseAuth.currentUser
            if (fbUser == null) {
                trySend(null)
            } else {
                firestore.collection("users").document(fbUser.uid).get()
                    .addOnSuccessListener { doc ->
                        val role = UserRole.fromString(doc.getString("role") ?: "CUSTOMER")
                        trySend(User(fbUser.uid, fbUser.email ?: "", doc.getString("name") ?: "", role))
                    }
                    .addOnFailureListener {
                        trySend(User(fbUser.uid, fbUser.email ?: "", fbUser.displayName ?: ""))
                    }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun login(email: String, password: String): Result<User> = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Login failed")
        val doc = firestore.collection("users").document(uid).get().await()
        User(uid, email, doc.getString("name") ?: "", UserRole.fromString(doc.getString("role") ?: "CUSTOMER"))
    }

    suspend fun register(email: String, password: String, name: String, role: UserRole = UserRole.CUSTOMER): Result<User> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Registration failed")
        val user = User(uid, email, name, role)
        firestore.collection("users").document(uid).set(
            mapOf("email" to email, "name" to name, "role" to role.name)
        ).await()
        user
    }

    fun logout() = auth.signOut()

    fun hasPermission(role: UserRole, required: UserRole): Boolean {
        val hierarchy = mapOf(UserRole.ADMIN to 3, UserRole.MANAGER to 2, UserRole.CUSTOMER to 1)
        return (hierarchy[role] ?: 0) >= (hierarchy[required] ?: 0)
    }
}

class ProductRepository(private val productDao: ProductDao) {
    private val firestore = FirebaseFirestore.getInstance()

    fun observeProducts(): Flow<List<Product>> = productDao.getAll().map { list ->
        list.map { Product(it.id, it.title, it.description, it.price, it.category, it.imageUrl, it.stock, it.rating) }
    }

    suspend fun syncProducts(): Result<Int> = runCatching {
        val apiProducts = ApiClient.api.getProducts()
        val entities = apiProducts.map { p ->
            ProductEntity(p.id, p.title, p.description, p.price, p.category, p.image, 100, p.rating.rate)
        }
        productDao.clearAll()
        productDao.insertAll(entities)
        firestore.collection("products_sync").document("latest").set(mapOf("count" to entities.size, "syncedAt" to System.currentTimeMillis()))
        entities.size
    }
}

class OrderRepository(private val orderDao: OrderDao) {
    private val firestore = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null
    private val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.IO)

    fun observeAllOrders(): Flow<List<Order>> = orderDao.getAll().map { list ->
        list.map { Order(it.id, it.customerId, it.customerName, it.productTitle, it.quantity, it.totalAmount, OrderStatus.valueOf(it.status), it.createdAt) }
    }

    fun observeCustomerOrders(customerId: String): Flow<List<Order>> = orderDao.getByCustomer(customerId).map { list ->
        list.map { Order(it.id, it.customerId, it.customerName, it.productTitle, it.quantity, it.totalAmount, OrderStatus.valueOf(it.status), it.createdAt) }
    }

    suspend fun placeOrder(order: Order): Result<Unit> = runCatching {
        val entity = OrderEntity(order.id, order.customerId, order.customerName, order.productTitle, order.quantity, order.totalAmount, order.status.name, order.createdAt)
        orderDao.insert(entity)
        firestore.collection("orders").document(order.id).set(
            mapOf(
                "customerId" to order.customerId,
                "customerName" to order.customerName,
                "productTitle" to order.productTitle,
                "quantity" to order.quantity,
                "totalAmount" to order.totalAmount,
                "status" to order.status.name,
                "createdAt" to order.createdAt
            )
        ).await()
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        orderDao.updateStatus(orderId, status.name)
        firestore.collection("orders").document(orderId).update("status", status.name).await()
    }

    fun startRealtimeSync(onUpdate: () -> Unit) {
        listener = firestore.collection("orders")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.documents?.forEach { doc ->
                    val entity = OrderEntity(
                        id = doc.id,
                        customerId = doc.getString("customerId") ?: "",
                        customerName = doc.getString("customerName") ?: "",
                        productTitle = doc.getString("productTitle") ?: "",
                        quantity = doc.getLong("quantity")?.toInt() ?: 1,
                        totalAmount = doc.getDouble("totalAmount") ?: 0.0,
                        status = doc.getString("status") ?: "PENDING",
                        createdAt = doc.getLong("createdAt") ?: 0L
                    )
                    scope.launch {
                        orderDao.insert(entity)
                        onUpdate()
                    }
                }
            }
    }

    fun stopRealtimeSync() { listener?.remove() }
}

class CustomerRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getCustomers(): Result<List<Customer>> = runCatching {
        val users = firestore.collection("users").get().await()
        val orders = firestore.collection("orders").get().await()
        users.documents.map { doc ->
            val uid = doc.id
            val userOrders = orders.documents.filter { it.getString("customerId") == uid }
            Customer(
                id = uid,
                name = doc.getString("name") ?: "",
                email = doc.getString("email") ?: "",
                totalOrders = userOrders.size,
                totalSpent = userOrders.sumOf { it.getDouble("totalAmount") ?: 0.0 }
            )
        }
    }
}

class AnalyticsRepository(
    private val analyticsDao: AnalyticsDao,
    private val orderDao: OrderDao,
    private val productDao: ProductDao
) {
    private val gson = Gson()

    fun observeAnalytics(): Flow<AnalyticsData?> = analyticsDao.getAnalytics().map { entity ->
        entity?.let {
            AnalyticsData(
                totalRevenue = it.totalRevenue,
                totalOrders = it.totalOrders,
                totalCustomers = it.totalCustomers,
                totalProducts = it.totalProducts,
                revenueByCategory = gson.fromJson(it.revenueJson, Map::class.java) as? Map<String, Double> ?: emptyMap(),
                ordersByStatus = gson.fromJson(it.ordersJson, Map::class.java) as? Map<String, Int> ?: emptyMap(),
                monthlyRevenue = gson.fromJson(it.monthlyJson, List::class.java) as? List<Double> ?: emptyList()
            )
        }
    }

    suspend fun refreshAnalytics(customerCount: Int): AnalyticsData {
        val allOrders = orderDao.getAll().first()
        val allProducts = productDao.getAll().first()

        val totalRevenue = allOrders.sumOf { it.totalAmount }
        val revenueByCategory = allProducts.groupBy { it.category }
            .mapValues { (_, prods) ->
                prods.sumOf { it.price } * allOrders.size.coerceAtLeast(1) / allProducts.size.coerceAtLeast(1)
            }
        val ordersByStatus = allOrders.groupBy { it.status }.mapValues { it.value.size }
        val monthlyRevenue = listOf(
            totalRevenue * 0.15, totalRevenue * 0.18, totalRevenue * 0.12,
            totalRevenue * 0.20, totalRevenue * 0.16, totalRevenue * 0.19
        )

        val data = AnalyticsData(
            totalRevenue = totalRevenue,
            totalOrders = allOrders.size,
            totalCustomers = customerCount,
            totalProducts = allProducts.size,
            revenueByCategory = revenueByCategory,
            ordersByStatus = ordersByStatus,
            monthlyRevenue = monthlyRevenue
        )

        analyticsDao.save(
            AnalyticsEntity(
                totalRevenue = data.totalRevenue,
                totalOrders = data.totalOrders,
                totalCustomers = data.totalCustomers,
                totalProducts = data.totalProducts,
                revenueJson = gson.toJson(data.revenueByCategory),
                ordersJson = gson.toJson(data.ordersByStatus),
                monthlyJson = gson.toJson(data.monthlyRevenue)
            )
        )
        return data
    }
}
