package com.example.letitcook.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.letitcook.data.local.entity.CommentEntity

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments")
    fun getAllComments(): List<CommentEntity>
    // TODO: check if it should be Comment and not CommentEntity

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun addComment(comment: CommentEntity)

    @Update
    suspend fun updateComment(comment: CommentEntity)

}