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

    /**
     * 1. GET POSTS (Observe Local DB)
     * This returns a stream from Room. Whenever we insert into Room,
     * this Flow automatically emits the new list to the UI.
     */
    fun getPostsFlow(): Flow<List<Post>> {
        return postDao.getAllPosts()
    }

    /**
     * 2. REFRESH (Fetch from Cloud -> Save to Local)
     * Call this when the app starts or user swipes to refresh.
     */
    suspend fun refreshPosts() {
        withContext(Dispatchers.IO) {
            try {
                val snapshot = db.collection(POSTS_COLLECTION)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val posts = snapshot.documents.mapNotNull { doc ->
                    val post = doc.toObject(Post::class.java)
                    post?.id = doc.id
                    post
                }

                // Save to Room (this triggers getPostsFlow automatically)
                if (posts.isNotEmpty()) {
                    postDao.insertAll(posts)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // If offline, we just don't update the local DB,
                // the user still sees old data from Room.
            }
        }
    }

    /**
     * 3. ADD POST
     * Uploads to Firebase, then saves to Local DB immediately for instant UI update.
     */
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
                    // Temporarily generate ID, Firestore will give real one later if we want strict consistency,
                    // but for now creating a random one works for local UI
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

                // 1. Upload to Firestore
                val docRef = db.collection(POSTS_COLLECTION).add(newPost).await()

                // 2. Update the ID to match Firestore's ID
                val finalPost = newPost.copy(id = docRef.id)

                // 3. Save to Room (UI updates instantly)
                postDao.insert(finalPost)

                Result(success = true)
            } catch (e: Exception) {
                e.printStackTrace()
                Result(success = false, errorMessage = e.message ?: "Failed to post")
            }
        }
    }
}