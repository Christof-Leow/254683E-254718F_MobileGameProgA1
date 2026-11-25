package com.nyp.scaffoldingapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nyp.scaffoldingapp.data.FoodItem
import com.nyp.scaffoldingapp.data.SampleData
import com.nyp.scaffoldingapp.ui.theme.ScaffoldingTheme

// reusable row for each food item (lazy list)
@Composable
fun FoodRow(
    food: FoodItem,
    isFavourite: Boolean,
    onSelect: () -> Unit,
    onToggleFavourite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // crops food image inside a rounded square
            OnlineImage(
                imageUrl = food.imageUrl,
                fallbackRes = food.imageRes,
                contentDescription = food.name,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${food.category} • ${food.prepMins} mins • ★ ${food.rating}" +
                            if (food.isHalal) " • Halal" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$${String.format("%.2f", food.price)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // button to toggle favourite star
            TextButton(onClick = onToggleFavourite) {
                Text(
                    text = if (isFavourite) "★" else "☆",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// preview for one food row
@Preview(showBackground = true)
@Composable
fun FoodRowPreview() {
    ScaffoldingTheme {
        FoodRow(
            food = SampleData.foods.first(),
            isFavourite = true,
            onSelect = {},
            onToggleFavourite = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}