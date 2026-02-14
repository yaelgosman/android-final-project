package com.example.letitcook.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.letitcook.data.local.AppLocalDb
import com.example.letitcook.data.local.entity.PostEntity
import com.example.letitcook.data.remote.firebase.PostsFirebaseService
import java.util.concurrent.Executors

class PostRepository {

    private val postDao = AppLocalDb.database.postDao()
    private val firebaseService = PostsFirebaseService()

    private val executor = Executors.newSingleThreadExecutor()

    companion object {
        val instance = PostRepository()
    }

    fun getAllPosts(): LiveData<List<PostEntity>> {
        // refresh from cloud
        refreshPosts()
        // get data from room
        return postDao.getAllPosts()
    }

    private fun refreshPosts() {
        firebaseService.getAllPosts { posts ->
            executor.execute {
                postDao.insertAll(posts)
            }
        }
    }

    fun addPost(post: PostEntity, imageUri: Uri?, callback: (Boolean) -> Unit) {
        firebaseService.addPost(post, imageUri) { success ->
            if (success) {
                // אם הצליח בענן -> שומרים גם מקומית
                // (הערה: כאן אנחנו שומרים את ה-URI המקומי או שנחכה לרענון הבא כדי לקבל את ה-URL מהענן.
                // כדי שהמשתמש יראה מייד, נשמור את הפוסט המקורי)
                executor.execute {
                    postDao.insert(post)
                }
            }
            callback(success)
        }
    }
}