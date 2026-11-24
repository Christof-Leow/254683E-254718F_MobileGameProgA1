package com.nyp.scaffoldingapp.data

import androidx.annotation.DrawableRes
import com.nyp.scaffoldingapp.R

// data class for one dish on the menu
data class FoodItem(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val rating: Double,
    val prepMins: Int,
    val isHalal: Boolean,
    val description: String,
    @DrawableRes val imageRes: Int
)

// data class for one restaurant outlet
data class Restaurant(
    val id: Int,
    val name: String,
    val address: String,
    val distanceKm: Double,
    val hours: String,
    val promoTag: String
)

// data class for one item inside the cart
data class CartItem(
    val food: FoodItem,
    val quantity: Int
)

// enum for pickup or delivery choice
enum class OrderMode(val label: String) {
    DELIVERY("Delivery"),
    PICKUP("Pickup")
}

// enum for live order tracking steps
enum class OrderStatus(val label: String, val progress: Float) {
    PLACED("Order Placed", 0.25f),
    PREPARING("Preparing in Kitchen", 0.6f),
    READY("Ready for Pickup or Delivery", 0.9f),
    COMPLETED("Completed", 1.0f)
}

// data class for a placed order
data class Order(
    val id: Int,
    val restaurantName: String,
    val itemCount: Int,
    val total: Double,
    val status: OrderStatus
)

// data class for one faq row
data class FaqItem(
    val id: Int,
    val question: String,
    val answer: String
)

// temporary sample data until mockapi is hooked up
object SampleData {
    val foods = listOf(
        // swap ic launcher foreground with your food drawables once added
        FoodItem(1, "Nasi Lemak", "Malay", 4.00, 4.7, 12, true, "Coconut rice with crispy ikan bilis, egg, peanuts, and sambal chilli.", R.drawable.food_nasi_lemak),        FoodItem(2, "Wonton", "Chinese", 5.60, 4.5, 10, false, "Crispy fried dumplings served with sweet chilli dipping sauce.", R.drawable.ic_launcher_foreground),
        FoodItem(3, "Laksa", "Local", 7.50, 4.8, 15, true, "Rich spicy coconut curry noodle soup topped with prawns and egg.", R.drawable.ic_launcher_foreground),
        FoodItem(4, "Chicken Rice", "Chinese", 5.00, 4.9, 10, true, "Poached chicken over fragrant garlic ginger rice with chilli sauce.", R.drawable.ic_launcher_foreground),
        FoodItem(5, "Bak Kut Teh", "Chinese", 6.20, 4.6, 18, false, "Traditional peppery herbal soup simmered until tender.", R.drawable.ic_launcher_foreground)
    )

    val restaurants = listOf(
        Restaurant(1, "Dash & Dine @ AMK St 44", "2 Ang Mo Kio Dr, Singapore 567720", 0.4, "8am - 10pm", "LUNCH HOUR 30% OFF"),
        Restaurant(2, "Malay Cuisine @ AMK Hub", "53 Ang Mo Kio Ave 3, Singapore 569933", 0.9, "9am - 10pm", "15% OFF AniqCoupon"),
        Restaurant(3, "Central Expw Express Stall", "10 Ang Mo Kio St 65, Singapore 569059", 1.5, "7am - 9pm", "FREE PICKUP")
    )

    val faqs = listOf(
        FaqItem(1, "How do I place an order?", "Pick an outlet in Nearby, add dishes from Home, and tap Proceed to Checkout in Cart."),
        FaqItem(2, "Can I switch between Delivery and Pickup?", "Yes, toggle Delivery or Pickup at the top of the Nearby or Cart page."),
        FaqItem(3, "How do I track my order?", "Check your active order progress bar inside the Profile tab after paying.")
    )
}