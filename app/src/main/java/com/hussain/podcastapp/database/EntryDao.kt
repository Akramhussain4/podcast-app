package com.hussain.podcastapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hussain.podcastapp.model.Entry

@Dao
interface EntryDao {

    @Query("SELECT * FROM entry")
    fun loadAllPodcasts(): List<Entry>

    @Insert
    fun insertPodcast(movies: Entry)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPodcastList(movies: List<Entry>)

    @Query("SELECT * FROM entry WHERE id = :id")
    fun getPodcast(id: String): Entry

    @Query("DELETE FROM entry WHERE id = :id")
    fun deletePodcast(id: String)
}
