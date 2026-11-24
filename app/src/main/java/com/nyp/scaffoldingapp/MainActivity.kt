package com.nyp.scaffoldingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.nyp.scaffoldingapp.ui.pages.*
import com.nyp.scaffoldingapp.ui.theme.ScaffoldingTheme
import practice.wk03.MockScreen

/**
 * The four bottom tabs. An enum keeps the list in one place — add a tab here
 * and the navigation bar updates itself.
 */
enum class Tab(val label: String, @DrawableRes val icon: Int) {
    HOME("Home", R.drawable.ic_home),
    NEARBY("Nearby", R.drawable.ic_list),
    CART("Cart", R.drawable.ic_star),
    PROFILE("Profile", R.drawable.ic_email)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScaffoldingTheme {
                // tracks if user is still on start screen
                var showStart by rememberSaveable { mutableStateOf(true) }

                if (showStart) {
                    StartScreenContent(
                        onGetStarted = {
                            // link login screen here later right now goes straight to main app
                            showStart = false
                        }
                    )
                } else {
                    AppScaffold(
                        onLogout = { showStart = true }
                    )
                }
            }
        }
    }
}

@Composable
fun AppScaffold(
    onLogout: () -> Unit = {}
) {
    // rememberSaveable, not remember: the selected tab must survive a rotation.
    var selectedTab by rememberSaveable { mutableStateOf(Tab.HOME) }

    // back from other tabs returns to home and back from home exits
    BackHandler(enabled = selectedTab != Tab.HOME) {
        selectedTab = Tab.HOME
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            // Icon() tints the image with the theme colour,
                            // which is what gives you selected / unselected
                            // states for free. Use Image() instead if you
                            // want the picture's own colours.
                            Icon(
                                painter = painterResource(tab.icon),
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        // Use the padding Scaffold gives you, or your content hides behind
        // the navigation bar.
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                Tab.HOME -> Page1()
                Tab.NEARBY -> Page2()
                Tab.CART -> Page3()
                Tab.PROFILE -> MockScreen()
            }
        }
    }
}