package com.infinity.lunadd.mvp.presenter.setting.impl;


import android.support.annotation.NonNull;

import com.avos.avoscloud.AVUser;
import com.hyphenate.easeui.domain.User;
import com.infinity.lunadd.base.BaseView;
import com.infinity.lunadd.mvp.model.rx.RxLeanCloud;
import com.infinity.lunadd.mvp.presenter.setting.IEditAlarmPresenter;
import com.infinity.lunadd.mvp.presenter.setting.IEditPreferLanguagePresenter;
import com.infinity.lunadd.mvp.view.setting.IEditAlarmView;
import com.infinity.lunadd.mvp.view.setting.IEditPreferLanguageView;
import com.infinity.lunadd.util.EaseSettingProvider;
import com.infinity.lunadd.util.PreferenceManager;
import com.orhanobut.logger.Logger;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by DanielChu on 2017/6/12.
 */
public class EditAlarmImpl implements IEditAlarmPresenter {

    private final PreferenceManager mPreferenceManager;
    private IEditAlarmView mEditAlarmView;
    private final EaseSettingProvider mEaseSettingProvider;

    @Inject
    public EditAlarmImpl(EaseSettingProvider easeSettingProvider, PreferenceManager preferenceManager) {
        mPreferenceManager = preferenceManager;
        mEaseSettingProvider = easeSettingProvider;
        Logger.init(this.getClass().getSimpleName());
    }

    @Override
    public void initData() {
        mEditAlarmView.setSwitch_msg(mEaseSettingProvider.getMsg());
        mEditAlarmView.setSwitch_song(mEaseSettingProvider.getSong());
        mEditAlarmView.setSwitch_vibrate(mEaseSettingProvider.getVibrate());
    }

    @Override
    public void changemsg(boolean ismsg) {
        mEaseSettingProvider.setMsg(ismsg);
        mPreferenceManager.setNotify(ismsg);
        mEditAlarmView.setSwitch_msg(ismsg);
    }

    @Override
    public void changesong(boolean issong) {
        mEaseSettingProvider.setSong(issong);
        mPreferenceManager.setIssong(issong);
        mEditAlarmView.setSwitch_song(issong);
    }

    @Override
    public void changevibrate(boolean isvibrate) {
        mEaseSettingProvider.setVibrate(isvibrate);
        mPreferenceManager.setIsshake(isvibrate);
        mEditAlarmView.setSwitch_vibrate(isvibrate);
    }


    @Override
    public void attachView(@NonNull BaseView view) {
        mEditAlarmView = (IEditAlarmView) view;
    }

    @Override
    public void detachView() {
        mEditAlarmView = null;
    }
}
