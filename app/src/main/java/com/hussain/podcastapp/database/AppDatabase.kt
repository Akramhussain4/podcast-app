package com.hussain.podcastapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hussain.podcastapp.model.Entry

@Database(entities = [Entry::class], version = 1, exportSchema = false)
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao

    companion object {

        private val LOCK = Any()
        private const val DATABASE_NAME = "podcasts"
        private var sInstance: AppDatabase? = null

        fun getInstance(context: Context?): AppDatabase? {
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = Room.databaseBuilder(context!!.applicationContext,
                            AppDatabase::class.java, AppDatabase.DATABASE_NAME)
                            .build()
                }
            }
            return sInstance
        }
    }
}