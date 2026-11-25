package com.nyp.scaffoldingapp.data

data class ApiCategory(
    val id: Int,
    val name: String,
    val sortOrder: Int,
    val isActive: Boolean
)

data class ApiFood(
    val id: Int,
    val categoryId: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val isHalal: Boolean,
    val isSpicy: Boolean,
    val allergens: List<String>,
    val rating: Double,
    val isActive: Boolean
)

data class ApiRestaurant(
    val id: Int,
    val name: String,
    val address: String,
    val postalCode: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String,
    val openingTime: String,
    val closingTime: String,
    val supportsPickup: Boolean,
    val supportsDelivery: Boolean,
    val deliveryFeeCents: Int,
    val deliveryRadiusKm: Double,
    val isActive: Boolean
)

data class ApiMenuItem(
    val id: Int,
    val restaurantId: Int,
    val foodId: Int,
    val pickupPriceCents: Int,
    val deliveryPriceCents: Int?,
    val prepMins: Int,
    val isAvailable: Boolean
)

data class ApiCoupon(
    val code: String,
    val discountType: String,
    val discountValue: Int,
    val minimumSpendCents: Int,
    val maximumDiscountCents: Int?,
    val categoryId: Int?,
    val foodId: Int?,
    val restaurantId: Int?,
    val orderType: String
)

data class ApiPromotion(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val startDate: String,
    val endDate: String,
    val isActive: Boolean,
    val showOnHome: Boolean,
    val coupon: ApiCoupon?
)