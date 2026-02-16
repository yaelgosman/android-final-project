package com.example.letitcook.models.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.letitcook.models.entity.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    // Return Flow so the UI updates automatically when DB changes
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<Post>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: Post)

    @Query("DELETE FROM posts")
    suspend fun clearAll()
}