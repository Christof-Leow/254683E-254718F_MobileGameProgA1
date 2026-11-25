package com.nyp.scaffoldingapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object DashApi {

    private const val BASE_URL = "https://asj80a.mockapi.dog/v1"
    private const val TIMEOUT_MS = 10000

    // get json data from the server
    private suspend fun getJson(path: String): JSONArray =
        withContext(Dispatchers.IO) {

            val url = URL("$BASE_URL/$path")
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = TIMEOUT_MS
                connection.readTimeout = TIMEOUT_MS

                if (connection.responseCode != 200) {
                    throw Exception("HTTP ${connection.responseCode}")
                }

                val response = connection.inputStream
                    .bufferedReader().use { it.readText() }

                JSONArray(response)

            } finally {
                connection.disconnect()
            }
        }

    // get all food categories
    suspend fun fetchCategories(): List<ApiCategory> {
        val json = getJson("categories")
        val categories = mutableListOf<ApiCategory>()

        for (i in 0 until json.length()) {
            val item = json.getJSONObject(i)

            categories.add(
                ApiCategory(
                    id = item.getInt("id"),
                    name = item.getString("name"),
                    sortOrder = item.getInt("sortOrder"),
                    isActive = item.getBoolean("isActive")
                )
            )
        }

        return categories
    }

    // get all food data
    suspend fun fetchFoods(): List<ApiFood> {
        val json = getJson("foods")
        val foods = mutableListOf<ApiFood>()

        for (i in 0 until json.length()) {
            val item = json.getJSONObject(i)

            val allergens = mutableListOf<String>()
            val allergenJson = item.getJSONArray("allergens")

            for (j in 0 until allergenJson.length()) {
                allergens.add(allergenJson.getString(j))
            }

            foods.add(
                ApiFood(
                    id = item.getInt("id"),
                    categoryId = item.getInt("categoryId"),
                    name = item.getString("name"),
                    description = item.getString("description"),
                    imageUrl = item.getString("imageUrl"),
                    isHalal = item.getBoolean("isHalal"),
                    isSpicy = item.getBoolean("isSpicy"),
                    allergens = allergens,
                    rating = item.getDouble("rating"),
                    isActive = item.getBoolean("isActive")
                )
            )
        }

        return foods
    }

    // get all restaurant outlets
    suspend fun fetchRestaurants(): List<ApiRestaurant> {
        val json = getJson("restaurants")
        val restaurants = mutableListOf<ApiRestaurant>()

        for (i in 0 until json.length()) {
            val item = json.getJSONObject(i)

            restaurants.add(
                ApiRestaurant(
                    id = item.getInt("id"),
                    name = item.getString("name"),
                    address = item.getString("address"),
                    postalCode = item.getString("postalCode"),
                    latitude = item.getDouble("latitude"),
                    longitude = item.getDouble("longitude"),
                    imageUrl = item.getString("imageUrl"),
                    openingTime = item.getString("openingTime"),
                    closingTime = item.getString("closingTime"),
                    supportsPickup = item.getBoolean("supportsPickup"),
                    supportsDelivery = item.getBoolean("supportsDelivery"),
                    deliveryFeeCents = item.getInt("deliveryFeeCents"),
                    deliveryRadiusKm = item.getDouble("deliveryRadiusKm"),
                    isActive = item.getBoolean("isActive")
                )
            )
        }

        return restaurants
    }

    // get food prices and availability for each outlet
    suspend fun fetchMenuItems(): List<ApiMenuItem> {
        val json = getJson("menuItems")
        val menuItems = mutableListOf<ApiMenuItem>()

        for (i in 0 until json.length()) {
            val item = json.getJSONObject(i)

            menuItems.add(
                ApiMenuItem(
                    id = item.getInt("id"),
                    restaurantId = item.getInt("restaurantId"),
                    foodId = item.getInt("foodId"),
                    pickupPriceCents = item.getInt("pickupPriceCents"),
                    deliveryPriceCents = getNullableInt(
                        item, "deliveryPriceCents"
                    ),
                    prepMins = item.getInt("prepMins"),
                    isAvailable = item.getBoolean("isAvailable")
                )
            )
        }

        return menuItems
    }

    // get all promotions and coupons
    suspend fun fetchPromotions(): List<ApiPromotion> {
        val json = getJson("promotions")
        val promotions = mutableListOf<ApiPromotion>()

        for (i in 0 until json.length()) {
            val item = json.getJSONObject(i)

            val coupon = if (item.isNull("coupon")) {
                null
            } else {
                parseCoupon(item.getJSONObject("coupon"))
            }

            promotions.add(
                ApiPromotion(
                    id = item.getInt("id"),
                    title = item.getString("title"),
                    description = item.getString("description"),
                    imageUrl = item.getString("imageUrl"),
                    startDate = item.getString("startDate"),
                    endDate = item.getString("endDate"),
                    isActive = item.getBoolean("isActive"),
                    showOnHome = item.getBoolean("showOnHome"),
                    coupon = coupon
                )
            )
        }

        return promotions
    }

    // get coupon data from the promotion
    private fun parseCoupon(item: JSONObject): ApiCoupon {
        return ApiCoupon(
            code = item.getString("code"),
            discountType = item.getString("discountType"),
            discountValue = item.getInt("discountValue"),
            minimumSpendCents = item.getInt("minimumSpendCents"),
            maximumDiscountCents = getNullableInt(
                item, "maximumDiscountCents"
            ),
            categoryId = getNullableInt(item, "categoryId"),
            foodId = getNullableInt(item, "foodId"),
            restaurantId = getNullableInt(item, "restaurantId"),
            orderType = item.getString("orderType")
        )
    }

    // some values from the api can be null
    private fun getNullableInt(
        item: JSONObject,
        key: String
    ): Int? {
        return if (item.isNull(key)) {
            null
        } else {
            item.getInt(key)
        }
    }
}