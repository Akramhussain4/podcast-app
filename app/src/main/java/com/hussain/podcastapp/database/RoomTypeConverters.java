package com.hussain.podcastapp.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hussain.podcastapp.model.PodcastImage;

import java.lang.reflect.Type;
import java.util.List;

public class RoomTypeConverters {

    @TypeConverter
    public static List<PodcastImage> stringToSomeObjectList(String data) {
        if (data == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<PodcastImage>>() {
        }.getType();
        List<PodcastImage> productCategoriesList = gson.fromJson(data, type);
        return productCategoriesList;
    }

    @TypeConverter
    public static String someObjectListToString(List<PodcastImage> someObjects) {
        Gson gson = new Gson();
        String ob = gson.toJson(someObjects);
        return ob;
    }
}
