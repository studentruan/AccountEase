package com.myapp.util;

import com.google.common.eventbus.EventBus;
import com.myapp.config.AppConfig;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18nUtil {
    private static ResourceBundle bundle;
    private static Locale currentLocale = Locale.ENGLISH;
    private static final EventBus eventBus = new EventBus();

    static {
        loadSavedLocale();
    }

    // 新增方法：获取当前ResourceBundle
    public static ResourceBundle getBundle() {
        if (bundle == null) {
            loadBundle();
        }
        return bundle;
    }

    // 新增方法：获取当前Locale
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    private static void loadSavedLocale() {
        String lang = AppConfig.get("language", "en");
        currentLocale = Locale.forLanguageTag(lang);
        loadBundle();
    }

    private static void loadBundle() {
        bundle = ResourceBundle.getBundle("lang/messages", currentLocale);
    }

    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }

    public static void setLocale(Locale locale) {
        if (!locale.equals(currentLocale)) {
            currentLocale = locale;
            AppConfig.save("language", locale.toLanguageTag());
            loadBundle();
            eventBus.post(new LocaleChangeEvent());
        }
    }

    public static void register(Object subscriber) {
        eventBus.register(subscriber);
    }

    public static class LocaleChangeEvent {}
}