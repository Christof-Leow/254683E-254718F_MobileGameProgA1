package com.nyp.scaffoldingapp.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
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
    var bitmap by remember(imageUrl) {
        mutableStateOf<Bitmap?>(null)
    }

    // download again when the image url changes
    LaunchedEffect(imageUrl) {

        if (imageUrl.isNotBlank()) {
            try {
                bitmap = DashApi.fetchImage(imageUrl)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                bitmap = null
            }
        }
    }

    val current = bitmap

    if (current != null) {
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