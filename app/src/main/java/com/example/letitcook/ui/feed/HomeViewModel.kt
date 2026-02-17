package com.example.letitcook.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.letitcook.data.PostRepository
import com.example.letitcook.models.entity.Post
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: PostRepository) : ViewModel() {

    // Convert Flow from Room to LiveData for the UI
    val posts: LiveData<List<Post>> = repository.getPostsFlow().asLiveData()

    init {
        refreshPosts()
    }

    fun refreshPosts() {
        viewModelScope.launch {
            // This pulls from Firebase and saves to Room.
            // Room then automatically updates 'val posts' above.
            repository.refreshPosts()
        }
    }
}

class HomeViewModelFactory(private val repository: PostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}