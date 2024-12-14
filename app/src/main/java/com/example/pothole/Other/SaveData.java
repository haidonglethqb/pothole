package com.example.pothole.Other;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveData {

    private static final String PREF_NAME = "PotholePrefs";
    private static final String KEY_POTHOLE_COUNT = "potholeCount";
    private static final String KEY_TOTAL_DISTANCE = "totalDistance";

    private final SharedPreferences sharedPreferences;

    public SaveData(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void savePotholeCount(int count) {
        sharedPreferences.edit().putInt(KEY_POTHOLE_COUNT, count).apply();
    }

    public int getPotholeCount() {
        return sharedPreferences.getInt(KEY_POTHOLE_COUNT, 0);
    }

    public void saveTotalDistance(double distance) {
        sharedPreferences.edit().putLong(KEY_TOTAL_DISTANCE, Double.doubleToRawLongBits(distance)).apply();
    }

    public double getTotalDistance() {
        return Double.longBitsToDouble(sharedPreferences.getLong(KEY_TOTAL_DISTANCE, Double.doubleToRawLongBits(0.0)));
    }
}
