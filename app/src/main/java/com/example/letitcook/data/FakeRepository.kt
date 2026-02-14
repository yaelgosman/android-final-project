package com.example.letitcook.data

data class Restaurant(
    val id: String,
    val name: String,
    val type: String,
    val rating: String,
    val imageUrl: String
)

object FakeRepository {
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
