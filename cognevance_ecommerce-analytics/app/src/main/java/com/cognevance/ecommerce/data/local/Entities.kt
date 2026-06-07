package com.cognevance.ecommerce.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products_cache")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String,
    val stock: Int,
    val rating: Double
)

@Entity(tableName = "orders_cache")
data class OrderEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val customerName: String,
    val productTitle: String,
    val quantity: Int,
    val totalAmount: Double,
    val status: String,
    val createdAt: Long
)

@Entity(tableName = "analytics_cache")
data class AnalyticsEntity(
    @PrimaryKey val id: Int = 1,
    val totalRevenue: Double,
    val totalOrders: Int,
    val totalCustomers: Int,
    val totalProducts: Int,
    val revenueJson: String,
    val ordersJson: String,
    val monthlyJson: String,
    val updatedAt: Long = System.currentTimeMillis()
)
