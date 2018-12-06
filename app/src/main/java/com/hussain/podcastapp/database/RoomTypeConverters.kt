package com.hussain.podcastapp.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hussain.podcastapp.model.PodcastImage

object RoomTypeConverters {

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<PodcastImage>? {
        if (data == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<PodcastImage>>() {

        }.type
        return gson.fromJson<List<PodcastImage>>(data, type)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<PodcastImage>): String {
        val gson = Gson()
        return gson.toJson(someObjects)
    }
}
