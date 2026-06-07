package com.cognevance.ecommerce.data.remote

import com.cognevance.ecommerce.domain.model.Product
import retrofit2.http.GET
import retrofit2.http.Path

data class ApiProduct(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val image: String,
    val rating: ApiRating = ApiRating()
)

data class ApiRating(val rate: Double = 0.0, val count: Int = 0)

interface EcommerceApi {
    @GET("products")
    suspend fun getProducts(): List<ApiProduct>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): ApiProduct

    @GET("products/category/{category}")
    suspend fun getByCategory(@Path("category") category: String): List<ApiProduct>
}

fun ApiProduct.toDomain(stock: Int = 100) = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    category = category,
    imageUrl = image,
    stock = stock,
    rating = rating.rate
)
