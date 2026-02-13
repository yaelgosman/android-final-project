package com.example.letitcook.models.Comments

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.letitcook.models.Comment

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments")
    fun getAllComments(): List<CommentEntity>
    // TODO: check if it should be Comment and not CommentEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addComment(comment: Comment)

    @Update
    suspend fun updateComment(comment: Comment)

}