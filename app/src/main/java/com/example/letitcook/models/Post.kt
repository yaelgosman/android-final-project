package com.example.letitcook.model

data class Post(
    val id: String,
    val userName: String,
    val location: String,
    val description: String,
    val rating: Float,
    val userAvatarUrl: String?,
    val postImageUrl: String?
)
