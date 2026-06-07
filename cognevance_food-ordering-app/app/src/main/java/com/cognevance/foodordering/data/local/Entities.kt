package com.cognevance.foodordering.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val foodId: Int,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int = 1
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val itemsJson: String,
    val totalAmount: Double,
    val paymentMethod: String,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val email: String,
    val displayName: String = "",
    val phone: String = "",
    val address: String = ""
)
