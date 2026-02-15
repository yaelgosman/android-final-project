package com.example.letitcook.data.remote.firebase

import android.net.Uri
import com.example.letitcook.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PostsFirebaseService {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val POSTS_COLLECTION = "posts"

    // 🔹 Get posts once (for refresh)
    suspend fun getAllPosts(): List<Post> {
        return try {
            val snapshot = db.collection(POSTS_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(30)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val post = doc.toObject(Post::class.java)
                post?.copy(id = doc.id)
            }

        } catch (e: Exception) {
            emptyList()
        }
    }

    // 🔹 Real time updates
    fun getPostsRealTime(): Flow<List<Post>> = callbackFlow {

        val listener = db.collection(POSTS_COLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    val post = doc.toObject(Post::class.java)
                    post?.copy(id = doc.id)
                } ?: emptyList()

                trySend(posts)
            }

        awaitClose { listener.remove() }
    }

    // 🔹 Add post
    suspend fun addPost(post: Post, imageUri: Uri?): Boolean {
        return try {

            var finalPost = post

            if (imageUri != null) {
                val filename = UUID.randomUUID().toString()
                val ref = storage.reference.child("post_images/$filename.jpg")

                ref.putFile(imageUri).await()
                val downloadUrl = ref.downloadUrl.await().toString()

                finalPost = post.copy(postImageUrl = downloadUrl)
            }

            db.collection(POSTS_COLLECTION)
                .document(finalPost.id)
                .set(finalPost)
                .await()

            true

        } catch (e: Exception) {
            false
        }
    }
}
