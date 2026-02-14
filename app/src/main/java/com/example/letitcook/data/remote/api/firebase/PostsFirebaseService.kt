package com.example.letitcook.data.remote.firebase

import android.net.Uri
import com.example.letitcook.data.local.entity.PostEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class PostsFirebaseService {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun getAllPosts(callback: (List<PostEntity>) -> Unit) {
        db.collection("posts")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(30)
            .get()
            .addOnSuccessListener { result ->
                val posts = result.map { document ->
                    PostEntity(
                        id = document.id,
                        userId = document.getString("userId") ?: "",
                        userName = document.getString("userName") ?: "",
                        userAvatarUrl = document.getString("userAvatarUrl") ?: "",
                        postImageUrl = document.getString("postImageUrl") ?: "",
                        restaurantName = document.getString("restaurantName") ?: "",
                        description = document.getString("description") ?: "",
                        rating = document.getDouble("rating")?.toFloat() ?: 0f,
                        timestamp = document.getLong("timestamp") ?: 0L
                    )
                }
                callback(posts)
            }
            .addOnFailureListener {
                callback(emptyList()) // במקרה של שגיאה נחזיר רשימה ריקה
            }
    }

    fun addPost(post: PostEntity, imageUri: Uri?, callback: (Boolean) -> Unit) {
        if (imageUri != null) {
            val filename = UUID.randomUUID().toString()
            val ref = storage.reference.child("images/$filename")

            ref.putFile(imageUri)
                .addOnSuccessListener {
                    // et image link after uploading
                    ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                        // update post's link
                        val postWithImage = post.copy(postImageUrl = downloadUrl.toString())
                        savePostToFirestore(postWithImage, callback)
                    }
                }
                .addOnFailureListener {
                    callback(false)
                }
        } else {
            savePostToFirestore(post, callback)
        }
    }

    private fun savePostToFirestore(post: PostEntity, callback: (Boolean) -> Unit) {
        val postMap = hashMapOf(
            "userId" to post.userId,
            "userName" to post.userName,
            "userAvatarUrl" to post.userAvatarUrl,
            "postImageUrl" to post.postImageUrl,
            "restaurantName" to post.restaurantName,
            "description" to post.description,
            "rating" to post.rating,
            "timestamp" to post.timestamp
        )

        db.collection("posts").document(post.id).set(postMap)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
}