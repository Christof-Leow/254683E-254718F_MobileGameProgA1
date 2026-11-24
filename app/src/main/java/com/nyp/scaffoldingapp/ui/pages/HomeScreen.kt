package com.nyp.scaffoldingapp.ui.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nyp.scaffoldingapp.data.FoodItem
import com.nyp.scaffoldingapp.data.SampleData
import com.nyp.scaffoldingapp.ui.components.FoodRow
import com.nyp.scaffoldingapp.ui.theme.ScaffoldingTheme

// home screen with promos search filters menu list and food detail
@Composable
fun HomeScreen(
    foods: List<FoodItem>,
    favouriteIds: Set<Int>,
    appliedCoupon: String?,
    onToggleFavourite: (Int) -> Unit,
    onApplyCoupon: (String) -> Unit,
    onAddToCart: (FoodItem, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var search by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    var selectedFoodId by rememberSaveable { mutableStateOf<Int?>(null) }
    var showPromoDialog by rememberSaveable { mutableStateOf(false) }

    val selectedFood = foods.find { it.id == selectedFoodId }

    // back from food detail goes back to menu list
    BackHandler(enabled = selectedFood != null) {
        selectedFoodId = null
    }

    if (selectedFood != null) {
        FoodDetailView(
            food = selectedFood,
            isFavourite = selectedFood.id in favouriteIds,
            onBack = { selectedFoodId = null },
            onToggleFavourite = { onToggleFavourite(selectedFood.id) },
            onAddToCart = { qty ->
                onAddToCart(selectedFood, qty)
                selectedFoodId = null
            },
            modifier = modifier
        )
    } else {
        val filters = listOf("All", "Halal", "Under $5", "Favourites")
        val filtered = foods.filter { item ->
            val matchesSearch = item.name.contains(search, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                "Halal" -> item.isHalal
                "Under $5" -> item.price < 5.0
                "Favourites" -> item.id in favouriteIds
                else -> true
            }
            matchesSearch && matchesFilter
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dash & Dine",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                // opens promo coupon popup
                TextButton(onClick = { showPromoDialog = true }) {
                    Text(if (appliedCoupon == null) "View Deal" else "15% Applied")
                }
            }

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("What food do you want?") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // horizontal row for sale promo banners (just placehodler for now)
            val promos = listOf(
                "CHICKEN RICE SALE!! 10% OFF!",
                "15% OFF MALAY CUISINE - AniqCoupon"
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(promos) { promo ->
                    Card(
                        onClick = { showPromoDialog = true },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.width(260.dp)
                    ) {
                        Text(
                            text = promo,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // horizontal filter buttons
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { filter ->
                    if (selectedFilter == filter) {
                        Button(
                            onClick = { selectedFilter = filter },
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) { Text(filter) }
                    } else {
                        OutlinedButton(
                            onClick = { selectedFilter = filter },
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) { Text(filter) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filtered.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("no dishes match ur filter.", style = MaterialTheme.typography.bodyLarge)
                    TextButton(onClick = {
                        search = ""
                        selectedFilter = "All"
                    }) {
                        Text("Reset Filters")
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filtered, key = { it.id }) { food ->
                        FoodRow(
                            food = food,
                            isFavourite = food.id in favouriteIds,
                            onSelect = { selectedFoodId = food.id },
                            onToggleFavourite = { onToggleFavourite(food.id) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }

    // popup dialog for promo coupon
    if (showPromoDialog) {
        AlertDialog( // alert dialogue draw the popup
            onDismissRequest = { showPromoDialog = false },
            title = { Text("15% OFF Deal") },
            text = { Text("Use coupon AniqCoupon for 15% off Malay Cuisines and local favourites.") },
            confirmButton = {
                Button(onClick = {
                    onApplyCoupon("AniqCoupon")
                    showPromoDialog = false
                }) {
                    Text("Apply Coupon")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPromoDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

// detail view when user taps a dish
@Composable
fun FoodDetailView(
    food: FoodItem,
    isFavourite: Boolean,
    onBack: () -> Unit,
    onToggleFavourite: () -> Unit,
    onAddToCart: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var quantity by rememberSaveable { mutableIntStateOf(1) }
    var showAddedDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onBack) { Text("Back") }
            TextButton(onClick = onToggleFavourite) {
                Text(if (isFavourite) "★ Saved" else "☆ Save")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Image(
            painter = painterResource(food.imageRes),
            contentDescription = food.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = food.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            // quantity stepper buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { if (quantity > 1) quantity-- }) { Text("-") }
                Text(
                    text = "$quantity",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                OutlinedButton(onClick = { quantity++ }) { Text("+") }
            }
        }

        Text(
            text = "★ ${food.rating} • ${food.prepMins} mins prep",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        Text(
            text = food.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$${String.format("%.2f", food.price * quantity)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { showAddedDialog = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(50.dp)
            ) {
                Text("Add To Cart", fontWeight = FontWeight.Bold)
            }
        }
    }

    // popup confirming item was added to cart
    if (showAddedDialog) {
        AlertDialog( // ALERT! (so got popup)
            onDismissRequest = { showAddedDialog = false },
            title = { Text("Order added to Cart") },
            text = { Text("${quantity}x ${food.name} added to your cart.") },
            confirmButton = {
                Button(onClick = {
                    showAddedDialog = false
                    onAddToCart(quantity)
                }) {
                    Text("Proceed")
                }
            }
        )
    }
}

// preview for home screen
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ScaffoldingTheme {
        HomeScreen(
            foods = SampleData.foods,
            favouriteIds = setOf(1, 3),
            appliedCoupon = null,
            onToggleFavourite = {},
            onApplyCoupon = {},
            onAddToCart = { _, _ -> }
        )
    }
}

// preview for food detail screen
@Preview(showBackground = true)
@Composable
fun FoodDetailPreview() {
    ScaffoldingTheme {
        FoodDetailView(
            food = SampleData.foods.first(),
            isFavourite = true,
            onBack = {},
            onToggleFavourite = {},
            onAddToCart = {}
        )
    }
}