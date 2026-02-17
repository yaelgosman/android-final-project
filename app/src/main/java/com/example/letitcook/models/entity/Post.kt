package com.example.letitcook.models.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "posts")
data class Post(
    @PrimaryKey
    var id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatarUrl: String? = null,
    val postImageUrl: String? = null,
    val location: String = "",
    val description: String = "",
    val rating: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable