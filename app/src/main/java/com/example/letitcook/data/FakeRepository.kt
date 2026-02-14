package com.example.letitcook.data

import android.net.Uri
import com.example.letitcook.model.Post
import com.example.letitcook.models.Restaurant
import java.util.UUID

object FakeRepository {

    private val posts = mutableListOf(
        Post(
            id = UUID.randomUUID().toString(),
            userName = "Guy Levi",
            location = "Burger Saloon • Tel Aviv",
            description = "Best burger in town 🍔",
            rating = 9.5f,
            userAvatarUrl = null,
            postImageUrl = null
        ),
        Post(
            id = UUID.randomUUID().toString(),
            userName = "Dana Cohen",
            location = "Pasta Basta • Haifa",
            description = "Amazing creamy pasta 🍝",
            rating = 8.7f,
            userAvatarUrl = null,
            postImageUrl = null
        )
    )

    fun getPosts(): List<Post> = posts

    fun addPost(
        userName: String,
        location: String,
        description: String,
        rating: Float,
        userAvatarUrl: Uri?,
        postImageUrl: Uri?
    ) {
        posts.add(
            0,
            Post(
                id = UUID.randomUUID().toString(),
                userName = userName,
                location = location,
                description = description,
                rating = rating,
                userAvatarUrl = userAvatarUrl?.toString(),
                postImageUrl = postImageUrl?.toString()
            )
        )
    }

    private val restaurants = listOf(
        Restaurant("1", "Raffaello", "Italian • $$$", "4.8", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd"),
        Restaurant("2", "Vitrina", "Hamburgers • $$", "4.7", "https://images.unsplash.com/photo-1555939594-58d7cb561ad1"),
        Restaurant("3", "Taizu", "Asian • $$$$", "4.9", "https://images.unsplash.com/photo-1561758033-d89a9ad46330"),
        Restaurant("4", "Ouzeria", "Mediterranean • $$", "4.5", "https://i.pravatar.cc/150?img=3")
    )

    fun getRestaurants(): List<Restaurant> = restaurants

    // פונקציה שתעזור לנו בספינר בהמשך
    fun getRestaurantNames(): List<String> {
        return restaurants.map { it.name }
    }
}
