package com.example.letitcook.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.letitcook.repositories.PostRepository
import com.example.letitcook.models.entity.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: PostRepository) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _userReviews = MutableStateFlow<List<Post>>(emptyList())
    val userReviews: StateFlow<List<Post>> = _userReviews

    // Call this when the Profile page loads, passing the current User's ID
    fun loadUserReviews(userId: String) {
        viewModelScope.launch {
            db.collection("posts")
                .whereEqualTo("userId", userId) // Filter: Get only this user's reviews
                .orderBy("timestamp", Query.Direction.DESCENDING) // Sort: Newest first
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val reviews = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(Post::class.java)?.copy(id = doc.id)
                        }
                        _userReviews.value = reviews
                    } else {
                        _userReviews.value = emptyList()
                    }
                }
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            repository.deletePost(post)
        }
    }
}