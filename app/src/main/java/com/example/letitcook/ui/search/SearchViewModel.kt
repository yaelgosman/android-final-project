package com.example.letitcook.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.letitcook.data.local.entity.PostEntity
import com.example.letitcook.data.repository.PostRepository
import kotlinx.coroutines.flow.Flow

class SearchViewModel : ViewModel() {
    private val repository = PostRepository.instance

    val posts: Flow<List<PostEntity>> = repository.getAllPosts()

    fun getCategories(): List<String> =
        listOf("All", "Italian", "Asian", "Meat", "Desserts")
}
