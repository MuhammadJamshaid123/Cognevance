package com.cognevance.ecommerce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cognevance.ecommerce.data.local.EcommerceDatabase
import com.cognevance.ecommerce.data.repository.AnalyticsRepository
import com.cognevance.ecommerce.data.repository.AuthRepository
import com.cognevance.ecommerce.data.repository.CustomerRepository
import com.cognevance.ecommerce.data.repository.OrderRepository
import com.cognevance.ecommerce.data.repository.ProductRepository
import com.cognevance.ecommerce.ui.EcommerceApp
import com.cognevance.ecommerce.ui.viewmodel.MainViewModel
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseAnalytics.getInstance(this)

        val db = EcommerceDatabase.getInstance(applicationContext)
        val authRepository = AuthRepository()
        val productRepository = ProductRepository(db.productDao())
        val orderRepository = OrderRepository(db.orderDao())
        val customerRepository = CustomerRepository()
        val analyticsRepository = AnalyticsRepository(db.analyticsDao(), db.orderDao(), db.productDao())

        setContent {
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModel.Factory(
                    authRepository, productRepository, orderRepository,
                    customerRepository, analyticsRepository
                )
            )
            EcommerceApp(viewModel)
        }
    }
}
