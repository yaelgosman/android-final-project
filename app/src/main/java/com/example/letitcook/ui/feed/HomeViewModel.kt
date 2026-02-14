package com.example.letitcook.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.letitcook.data.FakeRepository
import com.example.letitcook.data.PostRepository
import com.example.letitcook.model.Post
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: PostRepository) : ViewModel() {
//    private val repository = FakeRepository.getPosts()
    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    init {
//        refreshPosts()
        observeRealTimePosts()
    }

    fun refreshPosts() {
//        viewModelScope.launch {
//            _posts.postValue(repository.getPosts())
//        }
        viewModelScope.launch {
            try {
                val result = repository.getPosts()
                Log.d("FEED_DEBUG", "Posts fetched: ${result.size}") // Add this
                _posts.postValue(result)
            } catch (e: Exception) {
                Log.e("FEED_DEBUG", "Error: ${e.message}")
                _posts.postValue(emptyList())
            }
        }
    }

    private fun observeRealTimePosts() {
        viewModelScope.launch {
            // repository.getRealTimePosts() returns a Flow (a stream)
            repository.getPostsRealTime().collect { postList ->
                // Every time the DB changes, this block runs automatically
                _posts.postValue(postList)
            }
        }
    }

//    val posts: List<Post> = FakeRepository.getPosts()
}

// The "Manual Helper" that creates your ViewModel
class HomeViewModelFactory(private val repository: PostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}
