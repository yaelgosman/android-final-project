package com.example.letitcook.data

import androidx.annotation.WorkerThread
import com.example.letitcook.model.Post
import com.example.letitcook.models.Posts.PostDao

class PostRepository( private val postDao: PostDao ) {

    val allPosts: List<Post> = postDao.getAllPosts()

    @WorkerThread
    suspend fun addNewPost(post: Post) {
        postDao.addPost(post)
    }

//    TODO: add the other functions later!
}