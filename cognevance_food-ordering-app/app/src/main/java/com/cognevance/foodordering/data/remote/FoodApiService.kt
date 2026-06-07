package com.cognevance.foodordering.data.remote

import com.cognevance.foodordering.data.model.FoodItem
import com.cognevance.foodordering.data.model.OrderRequest
import com.cognevance.foodordering.data.model.OrderResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FoodApiService {
    @GET("products")
    suspend fun getProducts(): List<FoodItem>

    @GET("products/category/{category}")
    suspend fun getByCategory(@Path("category") category: String): List<FoodItem>

    @POST("orders")
    suspend fun placeOrder(@Body order: OrderRequest): OrderResponse
}
