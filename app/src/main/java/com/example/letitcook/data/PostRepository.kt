package com.example.letitcook.data

import android.content.Context
import android.net.Uri
import com.example.letitcook.models.AppLocalDb
import com.example.letitcook.models.entity.Post
import com.example.letitcook.utils.ImageUtils
import com.example.letitcook.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class PostRepository(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Initialize Room
    private val postDao = AppLocalDb.database.postDao()

    private val POSTS_COLLECTION = "posts"
    private val USERS_COLLECTION = "users"
    private val SAVED_COLLECTION = "saved_posts"

    // GET POSTS (Observe Local DB)
    fun getPostsFlow(): Flow<List<Post>> {
        return postDao.getAllPosts()
    }

    // GET SAVED POSTS (Observe Local DB)
    fun getSavedPosts(): Flow<List<Post>> {
        return postDao.getSavedPosts()
    }

    // REFRESH (Fetch from Cloud -> Save to Local)
    suspend fun refreshPosts() {
        withContext(Dispatchers.IO) {
            try {
                // Fetch Global Posts
                val snapshot = db.collection(POSTS_COLLECTION)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                // Fetch My Saved IDs
                val userId = auth.currentUser?.uid
                val savedIds = if (userId != null) {
                    val savedSnapshot = db.collection(USERS_COLLECTION)
                        .document(userId)
                        .collection(SAVED_COLLECTION)
                        .get()
                        .await()
                    savedSnapshot.documents.map { it.id }.toSet()
                } else {
                    emptySet()
                }

                // Merge Logic
                val posts = snapshot.documents.mapNotNull { doc ->
                    val post = doc.toObject(Post::class.java)
                    post?.id = doc.id

                    // Force isSaved to be based ONLY on the user's personal list.
                    // This prevents "Unsaving" issues if the global DB has 'true' by mistake.
                    post?.isSaved = savedIds.contains(post?.id)

                    post
                }

                // Save to Room
                if (posts.isNotEmpty()) {
                    postDao.insertAll(posts)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ADD POST
    suspend fun addPost(
        location: String,
        description: String,
        rating: Float,
        imageUri: Uri?
    ): Result {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = auth.currentUser
                    ?: return@withContext Result(success = false, errorMessage = "User not logged in")

                var downloadUrl: String? = null

                if (imageUri != null) {
                    val filename = UUID.randomUUID().toString()
                    val ref = storage.reference.child("post_images/$filename.jpg")
                    val imageData = ImageUtils.prepareImageForUpload(context, imageUri)
                    ref.putBytes(imageData).await()
                    downloadUrl = ref.downloadUrl.await().toString()
                }

                val newPost = Post(
                    id = UUID.randomUUID().toString(),
                    userId = currentUser.uid,
                    userName = currentUser.displayName ?: "Anonymous Chef",
                    userAvatarUrl = currentUser.photoUrl?.toString(),
                    postImageUrl = downloadUrl,
                    location = location,
                    description = description,
                    rating = rating,
                    timestamp = System.currentTimeMillis()
                )

                // Save to Firestore
                val docRef = db.collection(POSTS_COLLECTION).add(newPost).await()

                // Save to Room
                val finalPost = newPost.copy(id = docRef.id)
                postDao.insert(finalPost)

                Result(success = true)
            } catch (e: Exception) {
                e.printStackTrace()
                Result(success = false, errorMessage = e.message ?: "Failed to post")
            }
        }
    }

    suspend fun deletePost(post: Post) {
        // Delete from Room (Local)
        postDao.delete(post)

        // Delete from Firestore (Cloud)
        FirebaseFirestore.getInstance().collection("posts").document(post.id).delete()
    }

    suspend fun updatePost(post: Post, newImageUri: Uri?): Result {
        return try {
            var finalImageUrl = post.postImageUrl

            // If the user picked a NEW image, upload it first
            if (newImageUri != null) {
                val filename = UUID.randomUUID().toString()
                val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

                ref.putFile(newImageUri).await() // Upload
                val downloadUrl = ref.downloadUrl.await() // Get URL
                finalImageUrl = downloadUrl.toString()
            }

            val updates = mapOf(
                "description" to post.description,
                "location" to post.location,
                "rating" to post.rating,
                "postImageUrl" to (finalImageUrl ?: "")
            )

            // Update Firestore
            FirebaseFirestore.getInstance().collection("posts")
                .document(post.id)
                .update(updates)
                .await()

            // Update Room
            val updatedPostForLocalDb = post.copy(postImageUrl = finalImageUrl)
            postDao.insert(updatedPostForLocalDb)

            Result(success = true)

        } catch (e: Exception) {
            e.printStackTrace()
            Result(success = false, errorMessage = e.message)
        }
    }
    
    suspend fun toggleSave(post: Post) {
        val userId = auth.currentUser?.uid ?: return

        // Calculate the new status (Flip it)
        val isNowSaved = !post.isSaved

        // Create updated object
        val updatedPost = post.copy(isSaved = isNowSaved)

        // UPDATE ROOM (Local)
        // We use INSERT (Replace).
        // - If it's a Home post: It updates the existing row.
        // - If it's a Yelp post: It inserts a NEW row (Importing it to your DB).
        postDao.insert(updatedPost)

        // UPDATE FIREBASE (Remote)
        withContext(Dispatchers.IO) {
            try {
                val userSavedRef = db.collection(USERS_COLLECTION)
                    .document(userId)
                    .collection(SAVED_COLLECTION)
                    .document(post.id)

                if (isNowSaved) {
                    // Add link to User's collection
                    userSavedRef.set(mapOf("timestamp" to System.currentTimeMillis()))

                    db.collection(POSTS_COLLECTION).document(post.id).set(updatedPost)
                } else {
                    // Remove link from User's collection
                    userSavedRef.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}