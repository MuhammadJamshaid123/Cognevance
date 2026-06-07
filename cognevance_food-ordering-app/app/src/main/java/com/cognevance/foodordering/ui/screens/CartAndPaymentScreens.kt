package com.cognevance.foodordering.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cognevance.foodordering.ui.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onCheckout: () -> Unit
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val total = cartItems.sumOf { it.price * it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total: $${"%.2f".format(total)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Button(onClick = onCheckout) { Text("Checkout") }
                }
            }
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Your cart is empty")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
                items(cartItems, key = { it.foodId }) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold)
                                Text("$${"%.2f".format(item.price)} each")
                            }
                            IconButton(onClick = { cartViewModel.updateQuantity(item.foodId, item.quantity - 1) }) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }
                            Text("${item.quantity}", modifier = Modifier.padding(horizontal = 4.dp))
                            IconButton(onClick = { cartViewModel.updateQuantity(item.foodId, item.quantity + 1) }) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                            IconButton(onClick = { cartViewModel.removeItem(item.foodId) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    cartViewModel: CartViewModel,
    orderViewModel: com.cognevance.foodordering.ui.viewmodel.OrderViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val paymentState by orderViewModel.paymentState.collectAsState()
    val total = cartItems.sumOf { it.price * it.quantity }

    if (paymentState.isSuccess) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Payment Successful!", style = MaterialTheme.typography.headlineMedium)
                Text("Order confirmed for $${"%.2f".format(total)}")
                Spacer(Modifier.padding(16.dp))
                Button(onClick = onSuccess) { Text("View Orders") }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Total: $${"%.2f".format(total)}", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.padding(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Card", "Cash", "UPI").forEach { method ->
                    androidx.compose.material3.FilterChip(
                        selected = paymentState.paymentMethod == method,
                        onClick = { orderViewModel.setPaymentMethod(method) },
                        label = { Text(method) }
                    )
                }
            }
            if (paymentState.paymentMethod == "Card") {
                Spacer(Modifier.padding(8.dp))
                androidx.compose.material3.OutlinedTextField(
                    value = paymentState.cardNumber,
                    onValueChange = orderViewModel::updateCardNumber,
                    label = { Text("Card Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.padding(8.dp))
                androidx.compose.material3.OutlinedTextField(
                    value = paymentState.cardHolder,
                    onValueChange = orderViewModel::updateCardHolder,
                    label = { Text("Card Holder") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.padding(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.material3.OutlinedTextField(
                        value = paymentState.expiry,
                        onValueChange = orderViewModel::updateExpiry,
                        label = { Text("MM/YY") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    androidx.compose.material3.OutlinedTextField(
                        value = paymentState.cvv,
                        onValueChange = orderViewModel::updateCvv,
                        label = { Text("CVV") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
            paymentState.error?.let {
                Spacer(Modifier.padding(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.padding(16.dp))
            Button(
                onClick = { orderViewModel.processPayment(cartItems, onSuccess) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !paymentState.isProcessing
            ) {
                Text(if (paymentState.isProcessing) "Processing..." else "Pay $${"%.2f".format(total)}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    orderViewModel: com.cognevance.foodordering.ui.viewmodel.OrderViewModel,
    onBack: () -> Unit
) {
    val orders by orderViewModel.orders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (orders.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No orders yet")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
                items(orders, key = { it.id }) { order ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Order #${order.id}", fontWeight = FontWeight.Bold)
                            Text("Total: $${"%.2f".format(order.totalAmount)}")
                            Text("Payment: ${order.paymentMethod}")
                            Text("Status: ${order.status}")
                        }
                    }
                }
            }
        }
    }
}
