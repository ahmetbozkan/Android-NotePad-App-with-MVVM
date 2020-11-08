package com.example.notepadpro;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AlertDialog;

import java.util.Locale;

public class SettingsSharedPref {
    private static final String SETTINGS_SHARED_PREF = "SettingPrefs";
    public static final String IMAGE_PATH_KEY = "ImagePath";
    public static final String LANGUAGE_KEY = "Language";
    private static final String APP_THEME_KEY = "AppTheme";

    private static SharedPreferences instance;
    private Context mContext;

    public SettingsSharedPref(Context context) {
        mContext = context;

        if(instance == null) {
            instance = context.getSharedPreferences(SETTINGS_SHARED_PREF, Context.MODE_PRIVATE);
        }
    }

    public void saveImagePath(String imagePath) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putString(IMAGE_PATH_KEY, imagePath);
        editor.apply();
    }

    public String getImagePath() {
        return instance.getString(IMAGE_PATH_KEY, "");
    }

    public void saveLanguagePreference(String lang) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putString(LANGUAGE_KEY, lang);
        editor.apply();
    }

    public String getLanguage() {
        return instance.getString(LANGUAGE_KEY, "");
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        mContext.getResources().updateConfiguration(
                config,
                mContext.getResources().getDisplayMetrics()
        );

        saveLanguagePreference(lang);
    }

    public void saveAppTheme(boolean isDarkModeOn) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putBoolean(APP_THEME_KEY, isDarkModeOn);
        editor.apply();
    }

    public boolean getAppTheme() {
        return instance.getBoolean(APP_THEME_KEY, false);
    }
}
