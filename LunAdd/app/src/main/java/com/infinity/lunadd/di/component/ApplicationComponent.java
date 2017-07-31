package com.infinity.lunadd.di.component;

import android.content.Context;

import com.infinity.lunadd.LunApplication;
import com.infinity.lunadd.EaseUIHelper;
import com.infinity.lunadd.adapter.DurationAdapter;
import com.infinity.lunadd.di.module.ApplicationModule;
import com.infinity.lunadd.di.module.EaseUiModule;
import com.infinity.lunadd.di.scope.ContextLife;
import com.infinity.lunadd.mvp.model.rx.RxBus;
import com.infinity.lunadd.mvp.model.rx.RxLeanCloud;
import com.infinity.lunadd.receiver.DurationTimerReceiver;
import com.infinity.lunadd.receiver.PushReceiver;
import com.infinity.lunadd.util.EaseSettingProvider;
import com.infinity.lunadd.util.PreferenceManager;
import com.infinity.lunadd.util.SetUser;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by DanielChu on 2017/6/7.
 * <p>
 * Application容器，負責提供幾個全局使用到的對象
 */
@Singleton
@Component(modules = {ApplicationModule.class, EaseUiModule.class})
public interface ApplicationComponent {
    @ContextLife()
    Context getContext();

    PreferenceManager getPreferenceManager();

    SetUser getSetUser();

    RxLeanCloud getRxLeanCLoud();

    RxBus getRxBus();

    EaseUIHelper getEaseUiHelper();

    EaseSettingProvider getEaseSettingProvider();



    void inject(LunApplication lunApplication);

    void inject(PushReceiver pushReceiver);

    void inject(EaseUIHelper easeUIHelper);

    void inject(DurationTimerReceiver durationTimerReceiver);
}
