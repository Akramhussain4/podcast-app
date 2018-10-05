package com.hussain.podcastapp.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hussain.podcastapp.model.PodcastImage;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class RoomTypeConverters {

    @TypeConverter
    public static List<PodcastImage> stringToSomeObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<PodcastImage>>() {
        }.getType();
        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String someObjectListToString(List<PodcastImage> someObjects) {
        Gson gson = new Gson();
        return gson.toJson(someObjects);
    }
}
