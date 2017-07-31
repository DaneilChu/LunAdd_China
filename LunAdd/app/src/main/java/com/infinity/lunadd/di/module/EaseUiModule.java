package com.infinity.lunadd.di.module;

import android.content.Context;

import com.infinity.lunadd.EaseUIHelper;
import com.infinity.lunadd.di.scope.ContextLife;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by DanielChu on 2017/6/7.
 */

@Module
public class EaseUiModule {

    @Singleton
    @Provides
    public EaseUIHelper provideEaseUi(@ContextLife() Context context) {
        return new EaseUIHelper(context);
    }

}


