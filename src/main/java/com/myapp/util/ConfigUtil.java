package com.myapp.util;

import java.io.*;
import java.util.Properties;
import java.util.prefs.Preferences;

public class ConfigUtil {
    //Appearance设置内容
    private static final String CONFIG_FILE = "app_config.properties";
    // 主题设置
    public static String getThemePreference() {
        return getProperty("theme", "light");
    }
    public static void saveThemePreference(String theme) {
        setProperty("theme", theme);
    }
    // 数字字体设置
    public static String getNumberFontPreference() {
        return getProperty("numberFont", "default");
    }
    public static void saveNumberFontPreference(String font) {
        setProperty("numberFont", font);
    }
    // 语言字体设置
    public static String getLanguageFontPreference() {
        return getProperty("languageFont", "default");
    }
    public static void saveLanguageFontPreference(String font) {
        setProperty("languageFont", font);
    }
    // 通用方法
    private static String getProperty(String key, String defaultValue) {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty(key, defaultValue);
        } catch (IOException e) {
            return defaultValue;
        }
    }
    private static void setProperty(String key, String value) {
        Properties prop = new Properties();
        // 先加载现有配置
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            prop.load(input);
        } catch (IOException e) {
            // 文件不存在，创建新的
        }
        // 更新并保存
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            prop.setProperty(key, value);
            prop.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Function设置内容
    private static final Preferences prefs =
            Preferences.userNodeForPackage(ConfigUtil.class);

    public static boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public static void setBoolean(String key, boolean value) {
        prefs.putBoolean(key, value);
    }
}
