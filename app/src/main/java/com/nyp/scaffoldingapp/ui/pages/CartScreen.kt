package com.nyp.scaffoldingapp.ui.pages

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nyp.scaffoldingapp.data.CartItem
import com.nyp.scaffoldingapp.data.OrderMode
import com.nyp.scaffoldingapp.data.SampleData
import com.nyp.scaffoldingapp.ui.theme.ScaffoldingTheme

// cart screen showing selected outlet items and proceed button
@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    selectedRestaurantName: String,
    orderMode: OrderMode,
    appliedCoupon: String?,
    onUpdateQuantity: (Int, Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onBrowseMenu: () -> Unit,
    onProceedToCheckout: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val subtotal = cartItems.sumOf { it.food.price * it.quantity }
    val deliveryFee = if (orderMode == OrderMode.DELIVERY && cartItems.isNotEmpty()) 2.50 else 0.0
    val discount = if (appliedCoupon == "AniqCoupon") subtotal * 0.15 else 0.0 // placeholder
    val total = (subtotal - discount + deliveryFee).coerceAtLeast(0.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your Cart",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${orderMode.label} • $selectedRestaurantName",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (cartItems.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("ur cart empty", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBrowseMenu) {
                    Text("Browse Menu")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(cartItems, key = { it.food.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(item.food.imageRes),
                                contentDescription = item.food.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.food.name, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "$${String.format("%.2f", item.food.price)}",
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedButton(onClick = { onUpdateQuantity(item.food.id, -1) }) { Text("-") }
                                    Text("${item.quantity}", modifier = Modifier.padding(horizontal = 10.dp))
                                    OutlinedButton(onClick = { onUpdateQuantity(item.food.id, 1) }) { Text("+") }
                                }
                            }
                            TextButton(onClick = { onRemoveItem(item.food.id) }) {
                                Text("Remove")
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal")
                    Text("$${String.format("%.2f", subtotal)}")
                }
                if (discount > 0.0) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Coupon ($appliedCoupon)")
                        Text("-$${String.format("%.2f", discount)}")
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${orderMode.label} Fee")
                    Text("$${String.format("%.2f", deliveryFee)}")
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("$${String.format("%.2f", total)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        // later i link checkout screen here later
                        onProceedToCheckout(total)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Proceed to payment", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// preview for cart screen
@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    ScaffoldingTheme {
        CartScreen(
            cartItems = listOf(
                CartItem(SampleData.foods[0], 1),
                CartItem(SampleData.foods[2], 2)
            ),
            selectedRestaurantName = "Dash & Dine @ AMK St 44",
            orderMode = OrderMode.DELIVERY,
            appliedCoupon = "AniqCoupon",
            onUpdateQuantity = { _, _ -> },
            onRemoveItem = {},
            onBrowseMenu = {},
            onProceedToCheckout = {}
        )
    }
}