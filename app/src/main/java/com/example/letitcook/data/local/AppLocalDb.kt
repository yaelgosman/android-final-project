package com.example.letitcook.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.letitcook.LetItCookApp
import com.example.letitcook.data.local.dao.PostDao
import com.example.letitcook.data.local.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1)
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
            "letitcook-db.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}