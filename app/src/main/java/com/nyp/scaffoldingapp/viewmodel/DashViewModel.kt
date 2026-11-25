package com.nyp.scaffoldingapp.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyp.scaffoldingapp.R
import com.nyp.scaffoldingapp.data.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

sealed interface UiState {
    object Loading : UiState
    data class Ready(val data: DashData) : UiState
    data class Failed(val message: String) : UiState
}

// stores all the data from the api
data class DashData(
    val categories: List<ApiCategory>,
    val foods: List<ApiFood>,
    val restaurants: List<ApiRestaurant>,
    val menuItems: List<ApiMenuItem>,
    val promotions: List<ApiPromotion>
)

class DashViewModel : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state

    var selectedRestaurantId by mutableStateOf(1)
    var orderMode by mutableStateOf(OrderMode.DELIVERY)
    var favouriteIds by mutableStateOf(emptySet<Int>())
    var appliedCoupon by mutableStateOf<String?>(null)

    val cartItems = mutableStateListOf<CartItem>()
    val orders = mutableStateListOf<Order>()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading

            try {
                val categories = DashApi.fetchCategories()
                val foods = DashApi.fetchFoods()
                val restaurants = DashApi.fetchRestaurants()
                val menuItems = DashApi.fetchMenuItems()
                val promotions = DashApi.fetchPromotions()

                val data = DashData(
                    categories,
                    foods,
                    restaurants,
                    menuItems,
                    promotions
                )

                // select the first available outlet
                val activeRestaurants = restaurants.filter {
                    it.isActive && (it.supportsDelivery || it.supportsPickup)
                }

                if (activeRestaurants.isNotEmpty()) {
                    val restaurant = activeRestaurants[0]

                    selectRestaurant(restaurant)
                }

                _state.value = UiState.Ready(data)

            } catch (e: SocketTimeoutException) {
                _state.value = UiState.Failed(
                    "The server took too long to respond. Try again."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = UiState.Failed(
                    "Could not load data. Check your connection and try again."
                )
            }
        }
    }

    // get the food sold at the selected outlet
    fun getFoods(data: DashData): List<FoodItem> {
        val foods = mutableListOf<FoodItem>()

        val menu = data.menuItems.filter {
            it.restaurantId == selectedRestaurantId && it.isAvailable
        }

        for (menuItem in menu) {

            val food = data.foods.find {
                it.id == menuItem.foodId && it.isActive
            }

            if (food != null) {

                val category = data.categories.find {
                    it.id == food.categoryId && it.isActive
                }

                val price = if (orderMode == OrderMode.DELIVERY) {
                    menuItem.deliveryPriceCents
                } else {
                    menuItem.pickupPriceCents
                }

                if (category != null && price != null) {

                    // local images for now
                    val image = if (food.id == 101) {
                        R.drawable.food_nasi_lemak
                    } else {
                        R.drawable.ic_launcher_foreground
                    }

                    foods.add(
                        FoodItem(
                            id = food.id,
                            name = food.name,
                            category = category.name,
                            price = price / 100.0,
                            rating = food.rating,
                            prepMins = menuItem.prepMins,
                            isHalal = food.isHalal,
                            description = food.description,
                            imageRes = image,
                            imageUrl = food.imageUrl
                        )
                    )
                }
            }
        }

        return foods
    }

    // get all active outlets
    fun getRestaurants(data: DashData): List<Restaurant> {
        val restaurants = mutableListOf<Restaurant>()

        for (outlet in data.restaurants) {

            if (outlet.isActive && (outlet.supportsPickup || outlet.supportsDelivery)) {
                restaurants.add(
                    Restaurant(
                        id = outlet.id,
                        name = outlet.name,
                        address = "${outlet.address}, Singapore ${outlet.postalCode}",
                        distanceKm = 0.0,
                        hours = "${outlet.openingTime} - ${outlet.closingTime}",
                        promoTag = "",
                        imageUrl = outlet.imageUrl
                    )
                )
            }
        }

        return restaurants
    }

    fun selectRestaurant(restaurant: ApiRestaurant) {

        if (!restaurant.isActive) {
            return
        }

        if (!restaurant.supportsDelivery && !restaurant.supportsPickup) {
            return
        }

        // different outlets may have different food and prices
        if (selectedRestaurantId != restaurant.id) {
            cartItems.clear()
            appliedCoupon = null
        }

        selectedRestaurantId = restaurant.id

        if (!restaurant.supportsDelivery) {
            changeOrderMode(OrderMode.PICKUP)
        } else if (!restaurant.supportsPickup) {
            changeOrderMode(OrderMode.DELIVERY)
        }
    }

    fun changeOrderMode(mode: OrderMode) {

        if (orderMode != mode) {
            // pickup and delivery prices are different
            cartItems.clear()
            appliedCoupon = null

            orderMode = mode
        }
    }

    fun toggleFavourite(id: Int) {
        favouriteIds = if (id in favouriteIds) {
            favouriteIds - id
        } else {
            favouriteIds + id
        }
    }

    fun addToCart(food: FoodItem, qty: Int) {
        val index = cartItems.indexOfFirst {
            it.food.id == food.id
        }

        if (index >= 0) {
            cartItems[index] = cartItems[index].copy(
                quantity = cartItems[index].quantity + qty
            )
        } else {
            cartItems.add(CartItem(food, qty))
        }
    }

    fun updateQuantity(foodId: Int, delta: Int) {
        val index = cartItems.indexOfFirst {
            it.food.id == foodId
        }

        if (index >= 0) {
            val newQty = cartItems[index].quantity + delta

            if (newQty <= 0) {
                cartItems.removeAt(index)
            } else {
                cartItems[index] = cartItems[index].copy(
                    quantity = newQty
                )
            }
        }
    }

    fun removeItem(foodId: Int) {
        cartItems.removeAll {
            it.food.id == foodId
        }
    }

    // temporary order placement until partner checkout screen is linked
    fun placeOrder(restaurantName: String, total: Double) {

        if (cartItems.isNotEmpty()) {

            var itemCount = 0

            for (item in cartItems) {
                itemCount += item.quantity
            }

            orders.add(
                0,
                Order(
                    id = 100 + orders.size + 1,
                    restaurantName = restaurantName,
                    itemCount = itemCount,
                    total = total,
                    status = OrderStatus.PREPARING
                )
            )

            cartItems.clear()
            appliedCoupon = null
        }
    }

    fun logout() {
        cartItems.clear()
        orders.clear()
        favouriteIds = emptySet()
        appliedCoupon = null
    }
}