package com.infinity.lunadd.di.module;

import android.app.Application;
import android.content.Context;

import com.infinity.lunadd.di.scope.ContextLife;
import com.infinity.lunadd.mvp.model.rx.RxBus;
import com.infinity.lunadd.mvp.model.rx.RxLeanCloud;
import com.infinity.lunadd.util.EaseSettingProvider;
import com.infinity.lunadd.util.PreferenceManager;
import com.infinity.lunadd.util.SetUser;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by DanielChu on 2017/6/7.
 * 在Application里边生成的全局对象
 */
@Module
public class ApplicationModule {
    private final Application mApplication;
    private final PreferenceManager mPreferenceManager;
    private final RxLeanCloud mRxLeanCloud;
    private final RxBus mRxBus;
    private final SetUser mSetUser;
    private final EaseSettingProvider mEaseSettingProvider;

    public ApplicationModule(Application application) {
        mApplication = application;
        mPreferenceManager = new PreferenceManager(mApplication.getApplicationContext());
        mRxLeanCloud = new RxLeanCloud();
        mRxBus = new RxBus();
        mSetUser = new SetUser(mRxLeanCloud,mPreferenceManager);
        mEaseSettingProvider = new EaseSettingProvider();
    }

    @Provides
    @Singleton
    @ContextLife()
    public Context provideContext() {
        return mApplication.getApplicationContext();
    }


    @Provides
    @Singleton
    public PreferenceManager providePreferenceManager() {
        return mPreferenceManager;
    }

    @Provides
    @Singleton
    public SetUser provideSetUser() {
        return mSetUser;
    }

//    @Provides
//    @Singleton
//    public RxBabyRealm provideBabyRealm() {
//        return mRxBabyRealm;
//
//    }

    @Provides
    @Singleton
    public RxLeanCloud provideRxLeanCloud() {
        return mRxLeanCloud;
    }

    @Provides
    public EaseSettingProvider provideEaseSettingProvider() {
        return mEaseSettingProvider;
    }

    @Provides
    @Singleton
    public RxBus provideRxBus() {
        return mRxBus;
    }
}
