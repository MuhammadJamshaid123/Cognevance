package com.cognevance.ecommerce.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cognevance.ecommerce.domain.model.AnalyticsData
import com.cognevance.ecommerce.domain.model.OrderStatus
import com.cognevance.ecommerce.ui.viewmodel.MainViewModel

@Composable
fun AnalyticsDashboardScreen(viewModel: MainViewModel) {
    val analytics by viewModel.analytics.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Analytics Dashboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        analytics?.let { data ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Revenue", "$${"%.0f".format(data.totalRevenue)}", Icons.Default.Analytics, Modifier.weight(1f))
                StatCard("Orders", "${data.totalOrders}", Icons.Default.ShoppingBag, Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Customers", "${data.totalCustomers}", Icons.Default.People, Modifier.weight(1f))
                StatCard("Products", "${data.totalProducts}", Icons.Default.Store, Modifier.weight(1f))
            }
            Spacer(Modifier.height(24.dp))
            Text("Monthly Revenue", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            BarChart(data.monthlyRevenue)
            Spacer(Modifier.height(24.dp))
            Text("Orders by Status", style = MaterialTheme.typography.titleMedium)
            data.ordersByStatus.forEach { (status, count) ->
                Text("$status: $count orders", modifier = Modifier.padding(vertical = 4.dp))
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            androidx.compose.material3.Icon(icon, contentDescription = null, tint = Color(0xFF1565C0))
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BarChart(values: List<Double>) {
    val max = values.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0
    val colors = listOf(Color(0xFF1565C0), Color(0xFF42A5F5), Color(0xFF1976D2), Color(0xFF1E88E5), Color(0xFF2196F3), Color(0xFF64B5F6))
    Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
        val barWidth = size.width / (values.size * 2)
        values.forEachIndexed { index, value ->
            val barHeight = (value / max * size.height * 0.9).toFloat()
            drawRect(
                color = colors[index % colors.size],
                topLeft = Offset(index * barWidth * 2 + barWidth / 2, size.height - barHeight),
                size = Size(barWidth, barHeight)
            )
        }
    }
}

@Composable
fun ProductsScreen(viewModel: MainViewModel, isAdmin: Boolean) {
    val products by viewModel.products.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Products", style = MaterialTheme.typography.headlineMedium)
            if (isAdmin) {
                androidx.compose.material3.TextButton(onClick = { viewModel.syncProducts() }) {
                    Text("Sync API")
                }
            }
        }
        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(products, key = { it.id }) { product ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        coil.compose.AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.title,
                            modifier = Modifier.size(60.dp)
                        )
                        Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                            Text(product.title, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(product.category, style = MaterialTheme.typography.labelSmall)
                            Text("$${"%.2f".format(product.price)}", color = Color(0xFF1565C0))
                        }
                        if (!isAdmin) {
                            androidx.compose.material3.Button(onClick = { viewModel.placeOrder(product) }) {
                                Text("Buy")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrdersScreen(viewModel: MainViewModel, canManage: Boolean) {
    val orders by viewModel.orders.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Orders", style = MaterialTheme.typography.headlineMedium) }
        items(orders, key = { it.id }) { order ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(order.productTitle, fontWeight = FontWeight.Bold)
                    Text("Customer: ${order.customerName}")
                    Text("Amount: $${"%.2f".format(order.totalAmount)}")
                    Text("Status: ${order.status.name}")
                    if (canManage) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OrderStatus.entries.filter { it != OrderStatus.CANCELLED }.forEach { status ->
                                FilterChip(
                                    selected = order.status == status,
                                    onClick = { viewModel.updateOrderStatus(order.id, status) },
                                    label = { Text(status.name.take(4)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomersScreen(viewModel: MainViewModel) {
    val customers by viewModel.customers.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Customers", style = MaterialTheme.typography.headlineMedium)
                androidx.compose.material3.Button(onClick = { viewModel.loadCustomers() }) { Text("Refresh") }
            }
        }
        items(customers, key = { it.id }) { customer ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(customer.name, fontWeight = FontWeight.Bold)
                    Text(customer.email)
                    Text("Orders: ${customer.totalOrders} | Spent: $${"%.2f".format(customer.totalSpent)}")
                }
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: MainViewModel, onLoginSuccess: () -> Unit) {
    val state by viewModel.authState.collectAsState()
    val message by viewModel.message.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Cognevance Commerce", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Enterprise E-Commerce Platform", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(32.dp))
        androidx.compose.material3.OutlinedTextField(
            value = state.email, onValueChange = viewModel::updateEmail,
            label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        androidx.compose.material3.OutlinedTextField(
            value = state.password, onValueChange = viewModel::updatePassword,
            label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
        )
        message?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp)) }
        Spacer(Modifier.height(24.dp))
        androidx.compose.material3.Button(
            onClick = { viewModel.login(onLoginSuccess) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) { Text("Login") }
        Spacer(Modifier.height(8.dp))
        androidx.compose.material3.OutlinedButton(
            onClick = { viewModel.register(onLoginSuccess) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) { Text("Register as Customer") }
    }
}
