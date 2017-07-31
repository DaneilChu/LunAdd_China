package com.infinity.lunadd;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.PushService;
import com.hyphenate.easeui.domain.User;
import com.infinity.lunadd.mvp.model.bean.Comment;
import com.infinity.lunadd.mvp.model.bean.Post;
import com.infinity.lunadd.mvp.view.login.activity.LoginActivity;
import com.infinity.lunadd.mvp.view.setting.activity.EditPreferLanguageActivity;
import com.infinity.lunadd.util.LanguageUtil;
import com.infinity.lunadd.util.PreferenceManager;
import com.orhanobut.logger.AndroidLogTool;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.infinity.lunadd.di.component.ApplicationComponent;
import com.infinity.lunadd.di.component.DaggerApplicationComponent;
import com.infinity.lunadd.di.module.ApplicationModule;
import com.infinity.lunadd.mvp.view.home.activity.MainActivity;
import com.squareup.haha.perflib.Main;
import com.squareup.leakcanary.LeakCanary;

import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by Roger ou on 2016/3/25.
 * 初始化leancloud Easeui 第三方服务 leancloud
 * 接入Tinker，改造自己的Application
 */
public class LunApplication extends Application {
    private static ApplicationComponent mAppComponent;
    @Inject
    EaseUIHelper mEaseUIHelper;
    @Inject
    PreferenceManager mPreferenceManager;

    public LunApplication() {
        super();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LanguageUtil.updateConfig(this, newConfig);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initThirdService();
        initLanguage();
        mEaseUIHelper.init();
    }

    @Override
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(context);
    }

    private void initLanguage(){
        // get user preferred language set locale accordingly new locale(language,country)
        if (mPreferenceManager.getLanguage()==0) {
            LanguageUtil.setLocale(LanguageUtil.DEFAULT);
        }else{
            Logger.d("initLanguage");
            LanguageUtil.setLocale(mPreferenceManager.getLanguage());
        }
        Logger.d("UpdateLanguage");
        LanguageUtil.updateConfig(this, getBaseContext().getResources().getConfiguration());
        Logger.d("EndUpdateLanguage");
    }

    private void initThirdService() {
        AVUser.alwaysUseSubUserClass(User.class);
        AVObject.registerSubclass(Post.class);
        AVObject.registerSubclass(Comment.class);
        AVOSCloud.initialize(this, "yjlAIPjRRJHYBQfkwEeaTybY-gzGzoHsz", "sfNbFMRY5foTRt0PgixryY8m");
        AVAnalytics.enableCrashReport(this, true);
        initComponent();
        mAppComponent.inject(this);
        LeakCanary.install(this);
        Logger.init("Baby").logLevel(LogLevel.FULL).logTool(new AndroidLogTool());
        //TODO: Change to false after debugging
        AVOSCloud.setDebugLogEnabled(true);
        PushService.setDefaultPushCallback(this, MainActivity.class);
    }

    private void initComponent() {
        if (mAppComponent == null)
            mAppComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();

    }

    public static ApplicationComponent getAppComponent() {
        return mAppComponent;
    }

}
