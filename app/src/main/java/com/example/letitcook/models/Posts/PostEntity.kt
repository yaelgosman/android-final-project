package com.example.letitcook.models.Posts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity (
    @PrimaryKey val id: String,
    val userName: String,
    val location: String,
    val description: String,
    val rating: Float,
    val userAvatarUrl: String?,
    val postImageUrl: String?
)