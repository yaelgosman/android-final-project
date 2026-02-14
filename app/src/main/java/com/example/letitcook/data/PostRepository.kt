package com.example.letitcook.data

import android.content.Context
import android.net.Uri
import com.example.letitcook.model.Post
import com.example.letitcook.utils.ImageUtils
import com.example.letitcook.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PostRepository(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // This is the "Folder" in Firestore where all posts are saved
    private val POSTS_COLLECTION = "posts"

    // Add post
    suspend fun addPost(
        location: String,
        description: String,
        rating: Float,
        imageUri: Uri?
    ): Result {
        return try {
            val currentUser = auth.currentUser
                ?: return Result(success = false, errorMessage = "User not logged in")

            var downloadUrl: String? = null

            // Upload image (If one exists)
            if (imageUri != null) {
                // Generate a random filename (e.g., "post_images/random-uuid.jpg")
                val filename = UUID.randomUUID().toString()
                val ref = storage.reference.child("post_images/$filename.jpg")
                val imageData = ImageUtils.prepareImageForUpload(context, imageUri)

                ref.putBytes(imageData).await()
                downloadUrl = ref.downloadUrl.await().toString()
            }

            // Create the post object
            val post = Post(
                userId = currentUser.uid,
                userName = currentUser.displayName ?: "Anonymous Chef",
                userAvatarUrl = currentUser.photoUrl?.toString(),
                postImageUrl = downloadUrl,
                location = location,
                description = description,
                rating = rating,
                timestamp = System.currentTimeMillis()
            )

            // Save to the firestore
            // .add() automatically generates a unique Document ID
            db.collection(POSTS_COLLECTION).add(post).await()

            Result(success = true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result(success = false, errorMessage = e.message ?: "Failed to post")
        }
    }

    // Get posts (for the feed)
    suspend fun getPosts(): List<Post> {
        return try {
            val snapshot = db.collection(POSTS_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING) // Newest first
                .get()
                .await()

            // Convert documents to Post objects
            snapshot.documents.mapNotNull { doc ->
                val post = doc.toObject(Post::class.java)
                // Inject the Document ID into the object so we can use it later (e.g., for deleting)
                post?.id = doc.id
                post
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // TEST FUNCTION!!
    fun getPostsRealTime(): Flow<List<Post>> = callbackFlow {
        val subscription = db.collection(POSTS_COLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val posts = snapshot?.toObjects(Post::class.java) ?: emptyList()
                // Map the IDs like you did before
                snapshot?.documents?.forEachIndexed { index, doc ->
                    posts.getOrNull(index)?.id = doc.id
                }
                trySend(posts)
            }
        awaitClose { subscription.remove() }
    }
}