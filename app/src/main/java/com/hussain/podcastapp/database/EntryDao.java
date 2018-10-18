package com.hussain.podcastapp.database;

import com.hussain.podcastapp.model.Entry;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface EntryDao {

    @Query("SELECT * FROM entry")
    List<Entry> loadAllPodcasts();

    @Insert
    void insertPodcast(Entry movies);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPodcastList(List<Entry> movies);

    @Query("SELECT * FROM entry WHERE id = :id")
    Entry getPodcast(String id);

    @Query("DELETE FROM entry WHERE id = :id")
    void deletePodcast(String id);
}
