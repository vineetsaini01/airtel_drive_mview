package com.dashboard.roomdb;

import androidx.room.TypeConverter;

import com.dashboard.model.GraphData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Converters {
    @TypeConverter
    public static List<GraphData> fromString(String value) {
        return new Gson().fromJson(value, new TypeToken<List<GraphData>>() {}.getType());
    }

    @TypeConverter
    public static String fromList(List<GraphData> list) {
        return new Gson().toJson(list);
    }
}
