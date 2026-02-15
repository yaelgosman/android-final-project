package com.example.letitcook.model

data class Post(
    var id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatarUrl: String? = null,
    val postImageUrl: String? = null,
    val restaurantName: String = "",
    val description: String = "",
    val rating: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)