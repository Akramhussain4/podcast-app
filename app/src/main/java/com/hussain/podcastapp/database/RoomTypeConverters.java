package com.hussain.podcastapp.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hussain.podcastapp.model.PodcastImage;

import java.lang.reflect.Type;
import java.util.List;

import androidx.room.TypeConverter;

public class RoomTypeConverters {

    @TypeConverter
    public static List<PodcastImage> stringToSomeObjectList(String data) {
        if (data == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<PodcastImage>>() {
        }.getType();
        return gson.fromJson(data, type);
    }

    @TypeConverter
    public static String someObjectListToString(List<PodcastImage> someObjects) {
        Gson gson = new Gson();
        return gson.toJson(someObjects);
    }
}
