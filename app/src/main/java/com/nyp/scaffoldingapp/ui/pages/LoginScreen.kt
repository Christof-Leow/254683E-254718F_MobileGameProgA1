package com.nyp.scaffoldingapp.ui.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
//import com.nyp.scaffoldingapp.CurvedBottomShape
import com.nyp.scaffoldingapp.R
import com.nyp.scaffoldingapp.ui.theme.ScaffoldingTheme

class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScaffoldingTheme {
                LoginScreenContent(onLoggedIn = {})
            }
        }
    }
}

@Composable
fun LoginScreenContent(
    onLoggedIn: () -> Unit,
    modifier: Modifier = Modifier)
{
    // stacks the food top and text bottom
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // clips the pink box into the curved shape at the bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.45f)
                //.clip(CurvedBottomShape)
                //.background(MaterialTheme.colorScheme.primary)
        ) {
            // crops the food picture so it fills the whole curved box
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "Food banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // bottom half with app name tagline and button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // puts dash and dine side by side so dine can have the theme color
            Row {
                // makes the title bold
                Text(
                    text = "Dash & ",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Dine",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Run to fill your tummies",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp)
            )

            // empty space to push the button down to the bottom
            Spacer(modifier = Modifier.weight(1f))

            // rounds the button corners and adds a drop shadow underneath
            Button(
                onClick = onLoggedIn,
                modifier = Modifier
                    .fillMaxWidth()
                    // draws the drop shadow using the same rounded shape
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                // turns off the default button shadow so they don't clash
                elevation = null
            ) {
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ScaffoldingTheme {
        LoginScreenContent(onLoggedIn = {})
    }
}
