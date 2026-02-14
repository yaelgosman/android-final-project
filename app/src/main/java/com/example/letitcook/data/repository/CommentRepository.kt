package com.example.letitcook.data.repository

import androidx.annotation.WorkerThread
import com.example.letitcook.data.local.dao.CommentDao
import com.example.letitcook.data.local.entity.CommentEntity

class CommentRepository( private val commentDao: CommentDao) {

    val allComments: List<CommentEntity> = commentDao.getAllComments()

    @WorkerThread
    suspend fun addComment(comment: CommentEntity) {
        commentDao.addComment(comment)
    }

//    TODO: add the other functions later!
}