package com.nyp.scaffoldingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nyp.scaffoldingapp.ui.theme.ScaffoldingTheme

// start screen class that runs when the screen opens
class StartScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScaffoldingTheme {
                StartScreenContent()
            }
        }
    }
}

// composable function that draws the start screen
@Composable
fun StartScreenContent(
    modifier: Modifier = Modifier
) {
    // column stacks everything from top to bottom
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // top box with dark pink color to hold the food banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            // food image at the top
            // you can put your picture in res drawable and put its name here
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "Food banner",
                modifier = Modifier.fillMaxSize()
            )
        }

        // bottom column for words and button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // big title text for app name
            Text(
                text = "Dash & Dine",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            // small grey text under the title
            Text(
                text = "Run to fill your tummies",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // dark pink button for user to tap and start
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Get Started",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

// preview function so android studio can draw the screen without running the app
@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    ScaffoldingTheme {
        StartScreenContent()
    }
}