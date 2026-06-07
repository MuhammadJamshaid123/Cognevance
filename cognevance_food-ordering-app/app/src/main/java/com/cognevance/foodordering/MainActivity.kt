package com.cognevance.foodordering

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cognevance.foodordering.data.local.FoodDatabase
import com.cognevance.foodordering.data.repository.AuthRepository
import com.cognevance.foodordering.data.repository.CartRepository
import com.cognevance.foodordering.data.repository.FoodRepository
import com.cognevance.foodordering.data.repository.OrderRepository
import com.cognevance.foodordering.data.repository.UserRepository
import com.cognevance.foodordering.ui.FoodApp
import com.cognevance.foodordering.ui.viewmodel.AuthViewModel
import com.cognevance.foodordering.ui.viewmodel.CartViewModel
import com.cognevance.foodordering.ui.viewmodel.FoodViewModel
import com.cognevance.foodordering.ui.viewmodel.OrderViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = FoodDatabase.getInstance(applicationContext)
        val foodRepository = FoodRepository(applicationContext)
        val cartRepository = CartRepository(db.cartDao())
        val orderRepository = OrderRepository(db.orderDao(), db.cartDao())
        val authRepository = AuthRepository()
        val userRepository = UserRepository(db.userProfileDao())

        setContent {
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModel.Factory(authRepository, userRepository)
            )
            val foodViewModel: FoodViewModel = viewModel(
                factory = FoodViewModel.Factory(foodRepository)
            )
            val cartViewModel: CartViewModel = viewModel(
                factory = CartViewModel.Factory(cartRepository)
            )
            val orderViewModel: OrderViewModel = viewModel(
                factory = OrderViewModel.Factory(orderRepository, authRepository)
            )

            FoodApp(authViewModel, foodViewModel, cartViewModel, orderViewModel)
        }
    }
}
