package com.cognevance.ecommerce.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ProductEntity::class, OrderEntity::class, AnalyticsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EcommerceDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun analyticsDao(): AnalyticsDao

    companion object {
        @Volatile
        private var INSTANCE: EcommerceDatabase? = null

        fun getInstance(context: Context): EcommerceDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    EcommerceDatabase::class.java,
                    "cognevance_ecommerce_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
