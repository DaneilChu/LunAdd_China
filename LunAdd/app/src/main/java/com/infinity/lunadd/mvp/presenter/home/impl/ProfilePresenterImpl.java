package com.infinity.lunadd.mvp.presenter.home.impl;


import android.support.annotation.NonNull;

import com.infinity.lunadd.R;
import com.orhanobut.logger.Logger;
import com.infinity.lunadd.base.BaseView;
import com.infinity.lunadd.mvp.model.rx.RxLeanCloud;
import com.infinity.lunadd.mvp.presenter.home.IProfilePresenter;
import com.infinity.lunadd.mvp.view.home.IProfileView;
import com.infinity.lunadd.util.PreferenceManager;

import javax.inject.Inject;

/**
 * Created by DanielChu on 2017/6/9.
 */
public class ProfilePresenterImpl implements IProfilePresenter {

    private final PreferenceManager mPreferenceManager;
    private IProfileView mProfileView;

    @Inject
    public ProfilePresenterImpl(PreferenceManager preferenceManager) {
        mPreferenceManager = preferenceManager;
        Logger.init(this.getClass().getSimpleName());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void initData() {
        mProfileView.showLoading();
        if (mPreferenceManager.getCurrentUserName() != null) {
            mProfileView.showName(mPreferenceManager.getCurrentUserName());
        }
        if (mPreferenceManager.getPreferlanguageLanguage() != null) {
            mProfileView.showPreferLanguage(mPreferenceManager.getPreferlanguageLanguage());
        }
        mProfileView.hideProgress();
    }

    @Override
    public void onError() {
        mProfileView.showError();
    }


    @Override
    public void attachView(@NonNull BaseView view) {
        mProfileView = (IProfileView) view;
    }

    @Override
    public void detachView() {
        mProfileView = null;
    }
}
