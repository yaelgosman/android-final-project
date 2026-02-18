package com.example.letitcook.models

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.letitcook.LetItCookApp
import com.example.letitcook.models.dao.PostDao
import com.example.letitcook.models.entity.Post

@Database(entities = [Post::class], version = 2)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun postDao(): PostDao
}

object AppLocalDb {
    val database: AppLocalDbRepository by lazy {
        val context = LetItCookApp.Globals.appContext
            ?: throw IllegalStateException("Application context not initialized")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "letitcook-db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}