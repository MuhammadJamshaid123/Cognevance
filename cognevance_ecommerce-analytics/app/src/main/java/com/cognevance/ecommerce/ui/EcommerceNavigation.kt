package com.cognevance.ecommerce.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cognevance.ecommerce.domain.model.UserRole
import com.cognevance.ecommerce.ui.screens.AnalyticsDashboardScreen
import com.cognevance.ecommerce.ui.screens.CustomersScreen
import com.cognevance.ecommerce.ui.screens.LoginScreen
import com.cognevance.ecommerce.ui.screens.OrdersScreen
import com.cognevance.ecommerce.ui.screens.ProductsScreen
import com.cognevance.ecommerce.ui.viewmodel.MainViewModel

object Routes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val PRODUCTS = "products"
    const val ORDERS = "orders"
    const val CUSTOMERS = "customers"
    const val ANALYTICS = "analytics"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcommerceApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val user by viewModel.currentUser.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val message by viewModel.message.collectAsState()

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF1565C0))) {
        if (user == null) {
            LoginScreen(viewModel) {
                navController.navigate(Routes.PRODUCTS) { popUpTo(Routes.LOGIN) { inclusive = true } }
            }
        } else {
            val role = user!!.role
            val isAdmin = role == UserRole.ADMIN
            val isManager = role == UserRole.MANAGER || isAdmin

            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = { Text("Cognevance Commerce") },
                        actions = {
                            Text("${user!!.name} (${role.name})", modifier = Modifier.padding(end = 8.dp))
                            androidx.compose.material3.IconButton(onClick = { viewModel.logout() }) {
                                Icon(Icons.Default.Logout, contentDescription = "Logout")
                            }
                        }
                    )
                },
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate(Routes.PRODUCTS) },
                            icon = { Icon(Icons.Default.Store, contentDescription = null) },
                            label = { Text("Products") }
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate(Routes.ORDERS) },
                            icon = { Icon(Icons.Default.ShoppingBag, contentDescription = null) },
                            label = { Text("Orders") }
                        )
                        if (isManager) {
                            NavigationBarItem(
                                selected = false,
                                onClick = { navController.navigate(Routes.CUSTOMERS) },
                                icon = { Icon(Icons.Default.People, contentDescription = null) },
                                label = { Text("Customers") }
                            )
                            NavigationBarItem(
                                selected = false,
                                onClick = {
                                    viewModel.refreshAnalytics()
                                    navController.navigate(Routes.ANALYTICS)
                                },
                                icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                                label = { Text("Analytics") }
                            )
                        }
                    }
                }
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = Routes.PRODUCTS,
                    modifier = Modifier.fillMaxSize().padding(padding)
                ) {
                    composable(Routes.PRODUCTS) {
                        ProductsScreen(viewModel, isAdmin)
                    }
                    composable(Routes.ORDERS) {
                        OrdersScreen(viewModel, isManager)
                    }
                    composable(Routes.CUSTOMERS) {
                        if (isManager) CustomersScreen(viewModel)
                    }
                    composable(Routes.ANALYTICS) {
                        if (isManager) AnalyticsDashboardScreen(viewModel)
                    }
                }
            }
        }
    }
}
