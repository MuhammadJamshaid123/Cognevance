package com.cognevance.foodordering.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cognevance.foodordering.ui.screens.CartScreen
import com.cognevance.foodordering.ui.screens.HomeScreen
import com.cognevance.foodordering.ui.screens.LoginScreen
import com.cognevance.foodordering.ui.screens.OrdersScreen
import com.cognevance.foodordering.ui.screens.PaymentScreen
import com.cognevance.foodordering.ui.screens.SignupScreen
import com.cognevance.foodordering.ui.viewmodel.AuthViewModel
import com.cognevance.foodordering.ui.viewmodel.CartViewModel
import com.cognevance.foodordering.ui.viewmodel.FoodViewModel
import com.cognevance.foodordering.ui.viewmodel.OrderViewModel

object FoodRoutes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val HOME = "home"
    const val CART = "cart"
    const val PAYMENT = "payment"
    const val ORDERS = "orders"
}

private val FoodColorScheme = lightColorScheme(
    primary = Color(0xFFE65100),
    secondary = Color(0xFFFF9800)
)

@Composable
fun FoodApp(
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel
) {
    val navController = rememberNavController()
    val startDestination = if (authViewModel.isLoggedIn) FoodRoutes.HOME else FoodRoutes.LOGIN

    MaterialTheme(colorScheme = FoodColorScheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = startDestination) {
                composable(FoodRoutes.LOGIN) {
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateSignup = { navController.navigate(FoodRoutes.SIGNUP) },
                        onLoginSuccess = {
                            navController.navigate(FoodRoutes.HOME) {
                                popUpTo(FoodRoutes.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }
                composable(FoodRoutes.SIGNUP) {
                    SignupScreen(
                        viewModel = authViewModel,
                        onNavigateLogin = { navController.popBackStack() },
                        onSignupSuccess = {
                            navController.navigate(FoodRoutes.HOME) {
                                popUpTo(FoodRoutes.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }
                composable(FoodRoutes.HOME) {
                    HomeScreen(
                        foodViewModel = foodViewModel,
                        cartViewModel = cartViewModel,
                        onNavigateCart = { navController.navigate(FoodRoutes.CART) },
                        onNavigateOrders = { navController.navigate(FoodRoutes.ORDERS) },
                        onLogout = {
                            authViewModel.logout()
                            navController.navigate(FoodRoutes.LOGIN) {
                                popUpTo(FoodRoutes.HOME) { inclusive = true }
                            }
                        }
                    )
                }
                composable(FoodRoutes.CART) {
                    CartScreen(
                        cartViewModel = cartViewModel,
                        onBack = { navController.popBackStack() },
                        onCheckout = { navController.navigate(FoodRoutes.PAYMENT) }
                    )
                }
                composable(FoodRoutes.PAYMENT) {
                    PaymentScreen(
                        cartViewModel = cartViewModel,
                        orderViewModel = orderViewModel,
                        onBack = { navController.popBackStack() },
                        onSuccess = {
                            navController.navigate(FoodRoutes.ORDERS) {
                                popUpTo(FoodRoutes.HOME)
                            }
                        }
                    )
                }
                composable(FoodRoutes.ORDERS) {
                    OrdersScreen(
                        orderViewModel = orderViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
