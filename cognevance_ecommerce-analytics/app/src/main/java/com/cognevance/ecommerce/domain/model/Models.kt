package com.cognevance.ecommerce.domain.model

enum class UserRole {
    ADMIN,
    MANAGER,
    CUSTOMER;

    companion object {
        fun fromString(value: String): UserRole =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: CUSTOMER
    }
}

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val role: UserRole = UserRole.CUSTOMER
)

data class Product(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imageUrl: String = "",
    val stock: Int = 100,
    val rating: Double = 0.0
)

data class Customer(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val totalOrders: Int = 0,
    val totalSpent: Double = 0.0
)

data class Order(
    val id: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val productTitle: String = "",
    val quantity: Int = 1,
    val totalAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}

data class AnalyticsData(
    val totalRevenue: Double = 0.0,
    val totalOrders: Int = 0,
    val totalCustomers: Int = 0,
    val totalProducts: Int = 0,
    val revenueByCategory: Map<String, Double> = emptyMap(),
    val ordersByStatus: Map<String, Int> = emptyMap(),
    val monthlyRevenue: List<Double> = emptyList()
)
