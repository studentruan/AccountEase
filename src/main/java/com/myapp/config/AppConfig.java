package com.myapp.config;

import java.util.prefs.Preferences;

public class AppConfig {
    private static final Preferences prefs =
            Preferences.userNodeForPackage(AppConfig.class);

    public static void save(String key, String value) {
        prefs.put(key, value);
    }

    public static String get(String key, String defaultValue) {
        return prefs.get(key, defaultValue);
    }
}