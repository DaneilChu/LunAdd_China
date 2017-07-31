package com.infinity.lunadd.di.module;

import android.app.Activity;
import android.content.Context;

import com.infinity.lunadd.di.scope.ActivityScope;
import com.infinity.lunadd.di.scope.ContextLife;

import dagger.Module;
import dagger.Provides;

/**
 * Created by DanielChu on 2017/6/7.
 * 提供Activity和Context
 */
@Module
public class ActivityModule {

    private final Activity mActivity;

    public ActivityModule(Activity activity) {

        mActivity = activity;

    }

    @Provides
    @ActivityScope
    @ContextLife("Activity")
    public Context provideContext() {
        return mActivity;
    }

    @Provides
    @ActivityScope
    public Activity provideActivity() {
        return mActivity;
    }


}
