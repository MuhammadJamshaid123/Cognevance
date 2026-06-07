package com.cognevance.ecommerce.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products_cache ORDER BY title ASC")
    fun getAll(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("DELETE FROM products_cache")
    suspend fun clearAll()
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders_cache ORDER BY createdAt DESC")
    fun getAll(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders_cache WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getByCustomer(customerId: String): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderEntity)

    @Query("UPDATE orders_cache SET status = :status WHERE id = :orderId")
    suspend fun updateStatus(orderId: String, status: String)
}

@Dao
interface AnalyticsDao {
    @Query("SELECT * FROM analytics_cache WHERE id = 1")
    fun getAnalytics(): Flow<AnalyticsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(analytics: AnalyticsEntity)
}
