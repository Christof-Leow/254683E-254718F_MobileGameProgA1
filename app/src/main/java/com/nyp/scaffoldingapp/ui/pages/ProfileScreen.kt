package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nyp.scaffoldingapp.data.FaqItem
import com.nyp.scaffoldingapp.data.Order
import com.nyp.scaffoldingapp.data.OrderStatus
import com.nyp.scaffoldingapp.data.SampleData
import com.nyp.scaffoldingapp.ui.theme.ScaffoldingTheme

// profile screen with user info order tracker expandable faqs and logout
@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String,
    orders: List<Order>,
    faqs: List<FaqItem>,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedFaqId by rememberSaveable { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        item {
            Text(
                text = "Order history",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (orders.isEmpty()) {
            item {
                Text(
                    text = "No orders placed YET! Add food from house tab",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(orders, key = { it.id }) { order ->
                // animation for progress bar when order status updates
                val animatedProgress by animateFloatAsState(
                    targetValue = order.status.progress,
                    label = "order_progress"
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Order #${order.id} • ${order.restaurantName}",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            // to 2 dp or it will look weird on the screen
                            text = "${order.itemCount} items • $${String.format("%.2f", order.total)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = order.status.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "FAQs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(faqs, key = { it.id }) { faq ->
            val isExpanded = expandedFaqId == faq.id
            Card(
                onClick = { expandedFaqId = if (isExpanded) null else faq.id },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(faq.question, fontWeight = FontWeight.Bold)
                    AnimatedVisibility(visible = isExpanded) {
                        Text(
                            text = faq.answer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}

// preview for profile screen
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ScaffoldingTheme {
        ProfileScreen(
            userName = "Aniq",
            userEmail = "aniq@gmail.com",
            orders = listOf(
                Order(101, "Dash & Dine @ AMK St 44", 3, 15.18, OrderStatus.PREPARING)
            ),
            faqs = SampleData.faqs,
            onLogout = {}
        )
    }
}