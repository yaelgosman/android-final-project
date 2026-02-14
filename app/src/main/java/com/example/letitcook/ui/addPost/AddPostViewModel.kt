package com.example.letitcook.ui.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.letitcook.data.local.entity.PostEntity
import com.example.letitcook.data.repository.AuthRepository
import com.example.letitcook.data.repository.PostRepository
import java.util.UUID

class AddPostViewModel : ViewModel() {

    private val repository = PostRepository.instance
    private val authRepository = AuthRepository.instance

    fun addPost(
        restaurant: String,
        description: String,
        rating: Float,
        imageUri: Uri?,
        onComplete: (Boolean) -> Unit
    ) {
        val postId = UUID.randomUUID().toString()

        val post = PostEntity(
            id = postId,
            userId = authRepository.getCurrentUserId(),
            userName = authRepository.getCurrentUserName(),
            userAvatarUrl = "",
            postImageUrl = imageUri?.toString() ?: "",
            restaurantName = restaurant,
            description = description,
            rating = rating,
            timestamp = System.currentTimeMillis()
        )

        // הערה: הריפו צריך לדעת לטפל בהעלאת התמונה ל-Storage לפני שהוא שומר ב-Firestore
        repository.addPost(post, imageUri) { success ->
            onComplete(success)
        }
    }
}