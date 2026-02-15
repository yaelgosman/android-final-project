package com.example.letitcook.data.mapper

import com.example.letitcook.data.local.entity.PostEntity
import com.example.letitcook.model.Post

fun PostEntity.toPost(): Post =
    Post(
        id,
        userId,
        userName,
        userAvatarUrl,
        postImageUrl,
        restaurantName,
        description,
        rating,
        timestamp
    )

fun Post.toEntity(): PostEntity =
    PostEntity(
        id,
        userId,
        userName,
        userAvatarUrl,
        postImageUrl,
        restaurantName,
        description,
        rating,
        timestamp
    )
