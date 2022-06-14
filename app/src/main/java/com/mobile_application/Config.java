package com.mobile_application;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;
import java.util.Set;

public class Config {
    private static SharedPreferences preferences;
    private static  String configPath = "settings";
    private static final String TAG = "Config";

    public static SharedPreferences getConfig(Context context) {
        try {
            preferences = context.getSharedPreferences(configPath, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return preferences;
    }

    public static void setConfig(Context context, Map<String ,String> maps) {
        try {
            preferences = context.getSharedPreferences(configPath, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            Set<Map.Entry<String, String>> set = maps.entrySet();
            for (Map.Entry<String, String> map : set) {
                String key = map.getKey();
                String value = map.getValue();
                editor.putString(key, value);
            }
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "CONFIG FAIL");
        }
        Log.d(TAG, "CONFIG SUCCESS");
    }

    public static void clear(Context context) {
        try {
            preferences = context.getSharedPreferences(configPath, context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
