package com.example.letitcook.models

import com.google.gson.annotations.SerializedName

data class YelpSearchResponse(
    @SerializedName("businesses") val restaurants: List<YelpRestaurant>
)

data class YelpRestaurant(
    val id: String,
    val name: String,
    @SerializedName("image_url") val imageUrl: String?,
    val rating: Double,
    val location: YelpLocation?,
    val categories: List<YelpCategory>?
)

data class YelpLocation(
    val address1: String?,
    val city: String?
) {
    fun displayAddress(): String {
        return "${address1 ?: ""}, ${city ?: ""}"
    }
}

data class YelpCategory(
    val title: String // e.g., "Italian", "Burgers"
)