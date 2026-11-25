package com.nyp.scaffoldingapp.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nyp.scaffoldingapp.R
import com.nyp.scaffoldingapp.data.DashApi
import com.nyp.scaffoldingapp.ui.theme.ScaffoldingTheme
import kotlinx.coroutines.CancellationException

// reusable image that can load from the internet
@Composable
fun OnlineImage(
    imageUrl: String,
    fallbackRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    // check if the image already exists
    var bitmap by remember(imageUrl) {
        mutableStateOf<Bitmap?>(
            DashApi.getCachedImage(imageUrl)
        )
    }

    var isLoading by remember(imageUrl) {
        mutableStateOf(imageUrl.isNotBlank() && bitmap == null)
    }

    // only download if we do not have the image yet
    LaunchedEffect(imageUrl) {

        if (imageUrl.isNotBlank() && bitmap == null) {
            try {
                bitmap = DashApi.fetchImage(imageUrl)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                bitmap = null
            }
        }

        isLoading = false
    }

    val current = bitmap

    if (isLoading) {

        // show loading circle while downloading
        Box(
            modifier = modifier.background(
                MaterialTheme.colorScheme.surfaceVariant
            ),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        }

    } else if (current != null) {

        Image(
            bitmap = current.asImageBitmap(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )

    } else {

        Image(
            painter = painterResource(fallbackRes),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnlineImagePreview() {
    ScaffoldingTheme {
        OnlineImage(
            imageUrl = "",
            fallbackRes = R.drawable.food_nasi_lemak,
            contentDescription = "Food preview",
            modifier = Modifier.size(120.dp)
        )
    }
}