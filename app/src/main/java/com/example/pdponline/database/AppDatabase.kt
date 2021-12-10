package com.example.pdponline.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pdponline.dao.PDPDao
import com.example.pdponline.entities.Course
import com.example.pdponline.entities.Lesson
import com.example.pdponline.entities.Module

@Database(entities = [Course::class, Module::class, Lesson::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pdpDao(): PDPDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance =
                    Room.databaseBuilder(context, AppDatabase::class.java, "PdpAppRoomVersion")
                        .allowMainThreadQueries()
                        .build()
            }
            return instance!!
        }
    }
}