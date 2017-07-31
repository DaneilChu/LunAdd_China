package com.infinity.lunadd.di.component;

import android.app.Activity;
import android.content.Context;

import com.infinity.lunadd.di.module.ActivityModule;
import com.infinity.lunadd.di.scope.ActivityScope;
import com.infinity.lunadd.di.scope.ContextLife;
import com.infinity.lunadd.mvp.model.rx.RxBus;
import com.infinity.lunadd.mvp.model.rx.RxLeanCloud;
import com.infinity.lunadd.mvp.view.home.activity.AboutActivity;
import com.infinity.lunadd.mvp.view.home.activity.AddPostActivity;
import com.infinity.lunadd.mvp.view.home.activity.CommentActivity;
import com.infinity.lunadd.mvp.view.login.activity.LoginActivity;
import com.infinity.lunadd.mvp.view.setting.activity.EditNameActivity;
import com.infinity.lunadd.mvp.view.setting.activity.EditPreferLanguageActivity;
import com.infinity.lunadd.mvp.view.setting.activity.EditAlarmActivity;
import com.infinity.lunadd.mvp.view.home.activity.MainActivity;
import com.infinity.lunadd.mvp.view.setting.activity.EditLanguageActivity;
import com.infinity.lunadd.mvp.view.login.activity.LoginActivity;
import com.infinity.lunadd.util.EaseSettingProvider;
import com.infinity.lunadd.util.PreferenceManager;
import com.infinity.lunadd.util.SetUser;

import dagger.Component;

/**
 * Created by DanielChu on 2017/6/7.
 * Activity的容器，依賴ApplicationComponent
 * 負責Activity的注入對象的生命週期
 */
@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    @ContextLife("Activity")
    Context getActivityContext();

    @ContextLife()
    Context getApplicationContext();

    PreferenceManager getPreferenceManager();

    SetUser getSetUser();

    EaseSettingProvider getEaseSettingProvider();

    

//    RxBabyRealm getBabyRealm();

    Activity getActivity();

    RxLeanCloud getRxLeanCLoud();

    RxBus getRxBus();

    void inject(AboutActivity activity);

    void inject(EditNameActivity activity);

    void inject(EditPreferLanguageActivity activity);

    void inject(EditAlarmActivity activity);

    void inject(MainActivity activity);

    void inject(LoginActivity activity);

    void inject(EditLanguageActivity activity);

    void inject(AddPostActivity activity);

    void inject(CommentActivity activity);


    //  void inject(EditChooseSongActivity activity);
}
