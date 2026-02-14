package com.example.letitcook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userAvatarUrl: String,
    val postImageUrl: String,
    val restaurantName: String,
    val description: String,
    val rating: Float,
    val timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", "", "", "", 0f, 0L)
}