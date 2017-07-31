package com.infinity.lunadd.mvp.presenter.home.impl;


import android.support.annotation.NonNull;
import android.view.View;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.domain.UserDao;
import com.infinity.lunadd.EaseUIHelper;
import com.infinity.lunadd.base.BaseView;
import com.infinity.lunadd.mvp.presenter.home.IMainPresenter;
import com.infinity.lunadd.mvp.presenter.home.IProfilePresenter;
import com.infinity.lunadd.mvp.view.home.IMainView;
import com.infinity.lunadd.mvp.view.home.IProfileView;
import com.infinity.lunadd.util.PreferenceManager;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

/**
 * Created by DanielChu on 2017/6/15.
 */
public class MainImpl implements IMainPresenter {

    private final PreferenceManager mPreferenceManager;
    private IMainView mMainView;
    private EaseUIHelper mEaseUiHelper;
    private int duration;

    @Inject
    public MainImpl(PreferenceManager preferenceManager, EaseUIHelper easeUiHelper) {
        mPreferenceManager = preferenceManager;
        mEaseUiHelper=easeUiHelper;
        Logger.init(this.getClass().getSimpleName());
    }

    @Override
    public void attachView(@NonNull BaseView view) {
        mMainView = (IMainView) view;
    }

    @Override
    public void detachView() {
        mMainView = null;
    }

    /**
     * 获取未读消息数
     */
    @Override
    public int getUnread() {
        return mEaseUiHelper.getUnreadMessage();
    }

}
