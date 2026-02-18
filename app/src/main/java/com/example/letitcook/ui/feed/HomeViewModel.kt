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

    fun toggleSave(post: Post) {
        viewModelScope.launch {
            // We pass the ID and the *current* state.
            // The repository will flip it (true -> false, or false -> true)
            repository.toggleSave(post)
        }
    }
}

class HomeViewModelFactory(private val repository: PostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // If the Fragment asks for HomeViewModel
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        // If the Fragment asks for SavedViewModel
        if (modelClass.isAssignableFrom(com.example.letitcook.ui.saved.SavedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return com.example.letitcook.ui.saved.SavedViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}