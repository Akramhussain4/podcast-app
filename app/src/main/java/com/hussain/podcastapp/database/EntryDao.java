package com.hussain.podcastapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.hussain.podcastapp.model.Entry;

import java.util.List;

@Dao
public interface EntryDao {

    @Query("SELECT * FROM entry")
    List<Entry> loadAllPodcasts();

    @Insert
    void insertPodcast(Entry movies);

    @Query("SELECT * FROM entry WHERE id = :id")
    Entry getPodcast(String id);

    @Query("DELETE FROM entry WHERE id = :id")
    void deletePodcast(String id);
}
