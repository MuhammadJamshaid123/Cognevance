package com.cognevance.foodordering.data.model

data class FoodItem(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String,
    val rating: Double = 0.0
)

data class OrderRequest(
    val userId: String,
    val items: List<OrderItemRequest>,
    val totalAmount: Double,
    val paymentMethod: String,
    val status: String = "pending"
)

data class OrderItemRequest(
    val foodId: Int,
    val name: String,
    val quantity: Int,
    val price: Double
)

data class OrderResponse(
    val id: String = "",
    val userId: String = "",
    val totalAmount: Double = 0.0,
    val status: String = "",
    val createdAt: Long = 0L
)
