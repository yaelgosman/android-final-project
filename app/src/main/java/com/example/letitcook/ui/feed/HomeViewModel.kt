package com.example.letitcook.ui.feed

import androidx.lifecycle.ViewModel
import com.example.letitcook.data.FakeRepository
import com.example.letitcook.model.Post

class HomeViewModel : ViewModel() {
    private val repository = FakeRepository()

    val posts: List<Post> = repository.getPosts()
}
