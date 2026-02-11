package com.example.letitcook.data

import com.example.letitcook.model.Post

class FakeRepository {

    fun getPosts(): List<Post> {
        return listOf(
            Post(
                id = "1",
                userName = "Guy Levi",
                location = "Burger Saloon • Tel Aviv",
                description = "The best burger I had in years. Juicy, perfectly grilled, and the bun was amazing 🍔",
                rating = 9.5f,
                userAvatarUrl = null, // placeholder will be used
                postImageUrl = null   // placeholder will be used
            ),
            Post(
                id = "2",
                userName = "Dana Cohen",
                location = "Pasta Basta • Haifa",
                description = "Fresh pasta with creamy sauce. Simple but delicious 🍝",
                rating = 8.8f,
                userAvatarUrl = null,
                postImageUrl = null
            ),
            Post(
                id = "3",
                userName = "Noam Shahar",
                location = "Green Bowl • Ramat Gan",
                description = "Healthy, colorful and surprisingly filling. Highly recommended 🥗",
                rating = 9.1f,
                userAvatarUrl = null,
                postImageUrl = null
            )
        )
    }
}