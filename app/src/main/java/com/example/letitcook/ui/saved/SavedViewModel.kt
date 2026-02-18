package com.example.letitcook.ui.saved

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.letitcook.data.PostRepository
import com.example.letitcook.models.entity.Post
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SavedViewModel(private val repository: PostRepository) : ViewModel() {

    // 0 = All, 1 = Want to Go, 2 = Top Rated
    private val _filterType = MutableLiveData(0)

    val savedPosts: LiveData<List<Post>> = repository.getSavedPosts()
        .asLiveData()

    // We can do filtering in the Fragment for simplicity,
    // or use a MediatorLiveData here. Let's do it in the Fragment
    // so we don't overcomplicate the Flow logic right now.

    fun toggleSave(post: Post) {
        viewModelScope.launch {
            repository.toggleSave(post)
        }
    }
}