package com.example.letitcook.ui.search

import androidx.lifecycle.ViewModel
import com.example.letitcook.data.FakeRepository
import com.example.letitcook.model.Post

class SearchViewModel : ViewModel() {

    fun getPosts(): List<Post> = FakeRepository.getPosts()

    fun getCategories(): List<String> =
        listOf("All", "Italian", "Asian", "Meat", "Desserts")
}
