package com.nyp.scaffoldingapp.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.nyp.scaffoldingapp.data.OrderMode
import com.nyp.scaffoldingapp.data.Restaurant
import com.nyp.scaffoldingapp.data.SampleData
import com.nyp.scaffoldingapp.ui.theme.ScaffoldingTheme

// screen for searching nearby outlets and toggling delivery or pickup
@Composable
fun NearbyScreen(
    restaurants: List<Restaurant>,
    selectedRestaurantId: Int,
    orderMode: OrderMode,
    onChangeOrderMode: (OrderMode) -> Unit,
    onSelectRestaurant: (Restaurant) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filtered = restaurants.filter {
        it.name.contains(query, ignoreCase = true) ||
                it.address.contains(query, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Nearby Restaurants",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // delivery vs pickup toggle buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OrderMode.entries.forEach { mode ->
                if (orderMode == mode) {
                    Button(
                        onClick = { onChangeOrderMode(mode) },
                        modifier = Modifier.weight(1f)
                    ) { Text(mode.label) }
                } else {
                    OutlinedButton(
                        onClick = { onChangeOrderMode(mode) },
                        modifier = Modifier.weight(1f)
                    ) { Text(mode.label) }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search location e.g. Ang Mo Kio") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Text(
                text = "No nearby outlets match \"$query\".",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 24.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filtered, key = { it.id }) { outlet ->
                    val isSelected = outlet.id == selectedRestaurantId
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = outlet.promoTag,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = outlet.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Text(
                                text = outlet.address,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${outlet.distanceKm} km away • Open ${outlet.hours}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                            )

                            if (isSelected) {
                                OutlinedButton(
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Selected Outlet") }
                            } else {
                                Button(
                                    onClick = { onSelectRestaurant(outlet) },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Select for Order") }
                            }
                        }
                    }
                }
            }
        }
    }
}

// preview for nearby outlets screen
@Preview(showBackground = true)
@Composable
fun NearbyScreenPreview() {
    ScaffoldingTheme {
        NearbyScreen(
            restaurants = SampleData.restaurants,
            selectedRestaurantId = 1,
            orderMode = OrderMode.DELIVERY,
            onChangeOrderMode = {},
            onSelectRestaurant = {}
        )
    }
}