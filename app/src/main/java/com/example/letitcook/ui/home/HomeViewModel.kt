package com.example.letitcook.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.letitcook.data.local.entity.PostEntity
import com.example.letitcook.data.repository.PostRepository

class HomeViewModel : ViewModel() {
    private val repository = PostRepository.instance

    val posts: LiveData<List<PostEntity>> = repository.getAllPosts()

    fun refresh() {
        // הריפו עושה את זה אוטומטית ב-getAllPosts,
        // אבל אפשר לחשוף פונקציית refresh public בריפו אם רוצים
    }
}
