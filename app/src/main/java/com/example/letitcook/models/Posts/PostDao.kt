package com.example.letitcook.models.Posts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.letitcook.model.Post

@Dao
interface PostDao {

    @Query("SELECT * FROM posts")
    fun getAllPosts(): List<Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPost(post: Post)

    @Update
    suspend fun updatePost(post: Post)
}