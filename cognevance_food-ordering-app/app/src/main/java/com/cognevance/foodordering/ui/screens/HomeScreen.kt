package com.cognevance.foodordering.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cognevance.foodordering.data.model.FoodItem
import com.cognevance.foodordering.ui.viewmodel.CartViewModel
import com.cognevance.foodordering.ui.viewmodel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    foodViewModel: FoodViewModel,
    cartViewModel: CartViewModel,
    onNavigateCart: () -> Unit,
    onNavigateOrders: () -> Unit,
    onLogout: () -> Unit
) {
    val foodState by foodViewModel.uiState.collectAsState()
    val cartCount by cartViewModel.cartCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Menu") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE65100),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    TextButton(onClick = onNavigateOrders) {
                        Text("Orders", color = Color.White)
                    }
                    TextButton(onClick = onLogout) {
                        Text("Logout", color = Color.White)
                    }
                    BadgedBox(badge = {
                        if (cartCount > 0) Badge { Text("$cartCount") }
                    }) {
                        IconButton(onClick = onNavigateCart) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = foodState.searchQuery,
                onValueChange = foodViewModel::updateSearch,
                placeholder = { Text("Search food...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(foodState.categories) { category ->
                    FilterChip(
                        selected = foodState.selectedCategory == category,
                        onClick = { foodViewModel.selectCategory(category) },
                        label = { Text(category) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            when {
                foodState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                foodState.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(foodState.error ?: "Error")
                }
                foodState.filteredItems.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No items found")
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(foodState.filteredItems, key = { it.id }) { food ->
                        FoodCard(food = food, onAddToCart = { cartViewModel.addToCart(food) })
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodCard(food: FoodItem, onAddToCart: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = food.imageUrl,
                contentDescription = food.name,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(food.name, fontWeight = FontWeight.Bold)
                Text(food.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Text(" ${food.rating}", style = MaterialTheme.typography.labelSmall)
                    Text("  •  ${food.category}", style = MaterialTheme.typography.labelSmall)
                }
                Text("$${"%.2f".format(food.price)}", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
            }
            IconButton(onClick = onAddToCart) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = "Add to cart")
            }
        }
    }
}
