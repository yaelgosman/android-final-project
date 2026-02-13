package com.example.letitcook.data

import androidx.annotation.WorkerThread
import com.example.letitcook.models.Comment
import com.example.letitcook.models.Comments.CommentDao
import com.example.letitcook.models.Comments.CommentEntity

//TODO: check if this is the entity type we need for comment!

class CommentRepository( private val commentDao: CommentDao ) {

    val allComments: List<CommentEntity> = commentDao.getAllComments()

    @WorkerThread
    suspend fun addComment(comment: Comment) {
        commentDao.addComment(comment)
    }

//    TODO: add the other functions later!
}