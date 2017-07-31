package com.infinity.lunadd.mvp.presenter.setting.impl;


import android.support.annotation.NonNull;

import com.avos.avoscloud.AVUser;
import com.hyphenate.easeui.domain.User;
import com.infinity.lunadd.base.BaseView;
import com.infinity.lunadd.mvp.model.rx.RxLeanCloud;
import com.infinity.lunadd.mvp.presenter.setting.IEditLanguagePresenter;
import com.infinity.lunadd.mvp.presenter.setting.IEditPreferLanguagePresenter;
import com.infinity.lunadd.mvp.view.setting.IEditLanguageView;
import com.infinity.lunadd.mvp.view.setting.IEditPreferLanguageView;
import com.infinity.lunadd.util.LanguageUtil;
import com.infinity.lunadd.util.PreferenceManager;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by DanielChu on 2017/6/12.
 */
public class EditLanguageImpl implements IEditLanguagePresenter {

    private final PreferenceManager mPreferenceManager;
    private IEditLanguageView mEditLanguageView;

    @Inject
    public EditLanguageImpl(PreferenceManager preferenceManager) {
        mPreferenceManager = preferenceManager;
        Logger.init(this.getClass().getSimpleName());
    }

    @Override
    public void saveLanguage(final int language) {
        mPreferenceManager.setLanguage(language);
        LanguageUtil.setLocale(language);
        mEditLanguageView.toMainActivity();
    }

    @Override
    public void attachView(@NonNull BaseView view) {
        mEditLanguageView = (IEditLanguageView) view;
    }

    @Override
    public void detachView() {
        mEditLanguageView = null;
    }
}
