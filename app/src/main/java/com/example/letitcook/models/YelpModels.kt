package com.example.letitcook.models

import com.google.gson.annotations.SerializedName

// The top-level response object
data class YelpSearchResponse(
    @SerializedName("businesses") val restaurants: List<YelpRestaurant>
)

// The individual restaurant object
data class YelpRestaurant(
    val id: String,
    val name: String,
    @SerializedName("image_url") val imageUrl: String,
    val location: YelpLocation?
)

data class YelpLocation(
    val address1: String?,
    val city: String?
) {
    fun displayAddress(): String {
        return "$address1, $city"
    }
}