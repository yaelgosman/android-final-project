package com.example.letitcook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey var id: String,
    var userName: String,
    var text: String,
)