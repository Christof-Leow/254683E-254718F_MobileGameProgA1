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
import com.nyp.scaffoldingapp.data.*
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
    // rotation screws this up for some reason so i need this.
    var selectedTab by rememberSaveable { mutableStateOf(Tab.HOME) }

    // got this for now, temporary!!!
    var favouriteIds by remember { mutableStateOf(setOf(1, 3)) }
    var appliedCoupon by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedRestaurantId by rememberSaveable { mutableIntStateOf(1) }
    var orderMode by rememberSaveable { mutableStateOf(OrderMode.DELIVERY) }
    val cartItems = remember { mutableStateListOf<CartItem>() }
    val orders = remember { mutableStateListOf<Order>() }

    val selectedRestaurant = SampleData.restaurants.find { it.id == selectedRestaurantId }
        ?: SampleData.restaurants.first()

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
                Tab.HOME -> HomeScreen(
                    foods = SampleData.foods,
                    favouriteIds = favouriteIds,
                    appliedCoupon = appliedCoupon,
                    onToggleFavourite = { id ->
                        favouriteIds = if (id in favouriteIds) favouriteIds - id else favouriteIds + id
                    },
                    onApplyCoupon = { code -> appliedCoupon = code },
                    onAddToCart = { food, qty ->
                        val index = cartItems.indexOfFirst { it.food.id == food.id }
                        if (index >= 0) {
                            cartItems[index] = cartItems[index].copy(quantity = cartItems[index].quantity + qty)
                        } else {
                            cartItems.add(CartItem(food, qty))
                        }
                    }
                )
                Tab.NEARBY -> NearbyScreen(
                    restaurants = SampleData.restaurants,
                    selectedRestaurantId = selectedRestaurantId,
                    orderMode = orderMode,
                    onChangeOrderMode = { orderMode = it },
                    onSelectRestaurant = { selectedRestaurantId = it.id }
                )
                Tab.CART -> CartScreen(
                    cartItems = cartItems,
                    selectedRestaurantName = selectedRestaurant.name,
                    orderMode = orderMode,
                    appliedCoupon = appliedCoupon,
                    onUpdateQuantity = { foodId, delta ->
                        val index = cartItems.indexOfFirst { it.food.id == foodId }
                        if (index >= 0) {
                            val newQty = cartItems[index].quantity + delta
                            if (newQty <= 0) cartItems.removeAt(index)
                            else cartItems[index] = cartItems[index].copy(quantity = newQty)
                        }
                    },
                    onRemoveItem = { foodId ->
                        cartItems.removeAll { it.food.id == foodId }
                    },
                    onBrowseMenu = { selectedTab = Tab.HOME },
                    onProceedToCheckout = { total ->
                        // temporary order placement until partner checkout screen is linked
                        orders.add(
                            0,
                            Order(
                                id = 100 + orders.size + 1,
                                restaurantName = selectedRestaurant.name,
                                itemCount = cartItems.sumOf { it.quantity },
                                total = total,
                                status = OrderStatus.PREPARING
                            )
                        )
                        cartItems.clear()
                        selectedTab = Tab.PROFILE
                    }
                )
                Tab.PROFILE -> ProfileScreen(
                    userName = "Aniq", // placeholder, i dont even know if we are having account system
                    userEmail = "aniq@gmail.com",
                    orders = orders,
                    faqs = SampleData.faqs,
                    onLogout = onLogout
                )
            }
        }
    }
}