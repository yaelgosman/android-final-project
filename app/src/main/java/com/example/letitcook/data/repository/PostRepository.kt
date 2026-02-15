package com.example.letitcook.data.repository

import android.net.Uri
import androidx.lifecycle.map
import com.example.letitcook.data.local.AppLocalDb
import com.example.letitcook.data.mapper.toEntity
import com.example.letitcook.data.mapper.toPost
import com.example.letitcook.data.remote.firebase.PostsFirebaseService
import com.example.letitcook.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PostRepository {

    private val postDao = AppLocalDb.database.postDao()
    private val firebaseService = PostsFirebaseService()

    companion object {
        val instance = PostRepository()
    }

    // 🔹 Room is the source of truth
    fun getAllPosts(): Flow<List<Post>> {
        return postDao.getAllPosts()
            .map { entities ->
                entities.map { it.toPost() }
            } as Flow<List<Post>>
    }

    // 🔹 Manual refresh
    suspend fun refreshPosts() {
        val remotePosts = firebaseService.getAllPosts()
        postDao.insertAll(remotePosts.map { it.toEntity() })
    }

    // 🔹 Realtime sync Firebase → Room
    suspend fun observeRealtimePosts() {
        firebaseService.getPostsRealTime().collect { posts ->
            postDao.insertAll(posts.map { it.toEntity() })
        }
    }

    // 🔹 Add post
    suspend fun addPost(post: Post, imageUri: Uri?): Boolean {
        val success = firebaseService.addPost(post, imageUri)

        if (success) {
            postDao.insert(post.toEntity())
        }

        return success
    }
}
