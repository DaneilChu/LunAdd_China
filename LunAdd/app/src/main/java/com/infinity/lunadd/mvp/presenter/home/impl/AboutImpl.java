package com.infinity.lunadd.mvp.presenter.home.impl;

import android.support.annotation.NonNull;

import com.avos.avoscloud.AVUser;
import com.hyphenate.easeui.domain.User;
import com.infinity.lunadd.BuildConfig;
import com.infinity.lunadd.base.BaseView;
import com.infinity.lunadd.mvp.model.rx.RxLeanCloud;
import com.infinity.lunadd.mvp.presenter.home.IAboutPresenter;
import com.infinity.lunadd.mvp.presenter.setting.IEditNamePresenter;
import com.infinity.lunadd.mvp.view.home.IAboutView;
import com.infinity.lunadd.mvp.view.setting.IEditNameView;
import com.infinity.lunadd.util.PreferenceManager;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by DanielChu on 2017/6/13.
 */
public class AboutImpl implements IAboutPresenter {

    private IAboutView mAboutView;

    @Inject
    public AboutImpl() {
        Logger.init(this.getClass().getSimpleName());
    }

    @Override
    public void showCurrentVersionCode(){
        String versionName = BuildConfig.VERSION_NAME;
        mAboutView.showCurrentVersion(versionName);
    }

    @Override
    public void attachView(@NonNull BaseView view) {
        mAboutView = (IAboutView) view;
    }

    @Override
    public void detachView() {
        mAboutView = null;
    }
}
