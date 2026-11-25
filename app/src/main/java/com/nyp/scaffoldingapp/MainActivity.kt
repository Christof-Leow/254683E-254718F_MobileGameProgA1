package com.nyp.scaffoldingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyp.scaffoldingapp.data.*
import com.nyp.scaffoldingapp.ui.pages.*
import com.nyp.scaffoldingapp.ui.theme.ScaffoldingTheme
import com.nyp.scaffoldingapp.viewmodel.*

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
    // rotation screws this up for some reason so i need this.
    var selectedTab by rememberSaveable { mutableStateOf(Tab.HOME) }

    val viewModel: DashViewModel = viewModel()
    val state by viewModel.state.collectAsState()

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

            when (val current = state) {

                is UiState.Loading -> LoadingSkeleton()

                is UiState.Failed -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(current.message)

                        Spacer(Modifier.height(12.dp))

                        Button(onClick = { viewModel.load() }) {
                            Text("Retry")
                        }
                    }
                }

                is UiState.Ready -> {

                    val foods = viewModel.getFoods(current.data)
                    val restaurants = viewModel.getRestaurants(current.data)

                    val selectedRestaurant = current.data.restaurants.find {
                        it.id == viewModel.selectedRestaurantId && it.isActive
                    }

                    when (selectedTab) {

                        Tab.HOME -> HomeScreen(
                            foods = foods,
                            favouriteIds = viewModel.favouriteIds,
                            appliedCoupon = viewModel.appliedCoupon,
                            onToggleFavourite = { id ->
                                viewModel.toggleFavourite(id)
                            },
                            onApplyCoupon = { code ->
                                viewModel.appliedCoupon = code
                            },
                            onAddToCart = { food, qty ->
                                viewModel.addToCart(food, qty)
                            }
                        )

                        Tab.NEARBY -> NearbyScreen(
                            restaurants = restaurants,
                            selectedRestaurantId = viewModel.selectedRestaurantId,
                            orderMode = viewModel.orderMode,

                            onChangeOrderMode = { mode ->

                                if (selectedRestaurant != null) {
                                    if (mode == OrderMode.DELIVERY &&
                                        selectedRestaurant.supportsDelivery
                                    ) {
                                        viewModel.changeOrderMode(mode)
                                    } else if (mode == OrderMode.PICKUP &&
                                        selectedRestaurant.supportsPickup
                                    ) {
                                        viewModel.changeOrderMode(mode)
                                    }
                                }
                            },

                            onSelectRestaurant = { outlet ->

                                val restaurant = current.data.restaurants.find {
                                    it.id == outlet.id
                                }

                                if (restaurant != null) {
                                    viewModel.selectRestaurant(restaurant)
                                }
                            }
                        )

                        Tab.CART -> CartScreen(
                            cartItems = viewModel.cartItems,
                            selectedRestaurantName = selectedRestaurant?.name
                                ?: "No outlet selected",
                            orderMode = viewModel.orderMode,
                            appliedCoupon = viewModel.appliedCoupon,

                            onUpdateQuantity = { foodId, delta ->
                                viewModel.updateQuantity(foodId, delta)
                            },

                            onRemoveItem = { foodId ->
                                viewModel.removeItem(foodId)
                            },

                            onBrowseMenu = {
                                selectedTab = Tab.HOME
                            },

                            onProceedToCheckout = { total ->
                                // temporary order placement until partner checkout screen is linked
                                if (selectedRestaurant != null) {
                                    viewModel.placeOrder(
                                        selectedRestaurant.name,
                                        total
                                    )

                                    selectedTab = Tab.PROFILE
                                }
                            }
                        )

                        Tab.PROFILE -> ProfileScreen(
                            userName = "Aniq", // placeholder, i dont even know if we are having account system
                            userEmail = "aniq@gmail.com",
                            orders = viewModel.orders,
                            faqs = SampleData.faqs,
                            onLogout = {
                                viewModel.logout()
                                onLogout()
                            }
                        )
                    }
                }
            }
        }
    }
}

// placeholder boxes while waiting for the api
@Composable
fun LoadingSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "loading restaurant data..",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(16.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Spacer(Modifier.height(20.dp))

        for (i in 0 until 4) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )

                Spacer(Modifier.width(12.dp))

                Column {
                    Box(
                        Modifier
                            .fillMaxWidth(0.7f)
                            .height(20.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )

                    Spacer(Modifier.height(10.dp))

                    Box(
                        Modifier
                            .fillMaxWidth(0.4f)
                            .height(16.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingSkeletonPreview() {
    ScaffoldingTheme {
        LoadingSkeleton()
    }
}