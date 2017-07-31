package com.infinity.lunadd.util;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.ContextThemeWrapper;

import java.util.Locale;

/**
 * Created by DanielChu on 2017/6/9.
 */
public class LanguageUtil {

    private static Locale sLocale;
    public static final int DEFAULT = 0;
    public static final int SIMPLIFIED_CHINESE = 1;
    public static final int TRADITIONAL_CHINESE = 2;
    public static final int ENGLISH = 3;


    public static void setLocale(int language) {
        switch (language){
            case SIMPLIFIED_CHINESE:
                sLocale = Locale.SIMPLIFIED_CHINESE;
                break;
            case TRADITIONAL_CHINESE:
                sLocale = Locale.TRADITIONAL_CHINESE;
                break;
            case ENGLISH:
                sLocale = Locale.ENGLISH;
                break;
            case DEFAULT:
                sLocale = Locale.getDefault();
                break;
        }
        if(sLocale != null) {
            Locale.setDefault(sLocale);
        }
    }

    public static void updateConfig(ContextThemeWrapper wrapper) {
        if(sLocale != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Configuration configuration = new Configuration();
            configuration.setLocale(sLocale);
            wrapper.applyOverrideConfiguration(configuration);
        }
    }

    public static void updateConfig(Application app, Configuration configuration) {
        if(sLocale != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //Wrapping the configuration to avoid Activity endless loop
            Configuration config = new Configuration(configuration);
            config.locale = sLocale;
            Resources res = app.getBaseContext().getResources();
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
    }

}