package com.example.letitcook.models.Comments

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey var id: String,
    var userName: String,
    var text: String,
)
