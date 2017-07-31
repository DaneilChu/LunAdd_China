package com.infinity.lunadd.mvp.presenter.setting.impl;


import android.support.annotation.NonNull;

import com.avos.avoscloud.AVUser;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.domain.UserDao;
import com.infinity.lunadd.base.BaseView;
import com.infinity.lunadd.mvp.model.rx.RxLeanCloud;
import com.infinity.lunadd.mvp.presenter.setting.IEditPreferLanguagePresenter;
import com.infinity.lunadd.mvp.view.setting.IEditPreferLanguageView;
import com.infinity.lunadd.util.PreferenceManager;
import com.orhanobut.logger.Logger;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by DanielChu on 2017/6/12.
 */
public class EditPreferLanguageImpl implements IEditPreferLanguagePresenter {

    private final PreferenceManager mPreferenceManager;
    private IEditPreferLanguageView mEditPreferLanguageView;
    private final RxLeanCloud mRxLeanCloud;

    @Inject
    public EditPreferLanguageImpl(RxLeanCloud rxLeanCloud, PreferenceManager preferenceManager) {
        mPreferenceManager = preferenceManager;
        mRxLeanCloud = rxLeanCloud;
        Logger.init(this.getClass().getSimpleName());
    }

    @Override
    public void initData() {
        if (mPreferenceManager.getPreferlanguageLanguage() != null) {
            mEditPreferLanguageView.showSelectedLanguage(mPreferenceManager.getPreferlanguageLanguage());
        }
    }


    @Override
    public void savePreferLanguage(final List<String> preferlanguage) {
        mEditPreferLanguageView.showUpdating();
        Logger.d("EditPreferLanguage");
        final AVUser avUser = AVUser.getCurrentUser();
        if (avUser!=null) {
            if (preferlanguage == null || preferlanguage.isEmpty()) {
                mEditPreferLanguageView.showEmptyListMessage();
            } else {
                avUser.put(UserDao.PREFERLANGUAGE,preferlanguage);
                mRxLeanCloud.SaveUserByLeanCloud(avUser)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<AVUser>() {
                            @Override
                            public void onCompleted() {
                                mEditPreferLanguageView.hideProgress();
                                mPreferenceManager.setPreferlanguageLanguage(preferlanguage);
                                mEditPreferLanguageView.close();
                            }

                            @Override
                            public void onError(Throwable e) {
                                mEditPreferLanguageView.showError();
                            }

                            @Override
                            public void onNext(AVUser user) {
                                if (user != null) {
                                    mEditPreferLanguageView.setResultcode();
                                }
                            }
                        });
            }
        }else{
            mEditPreferLanguageView.setResultcode();
            mEditPreferLanguageView.hideProgress();
            mPreferenceManager.setPreferlanguageLanguage(preferlanguage);
            mEditPreferLanguageView.close();
        }
    }

    @Override
    public void attachView(@NonNull BaseView view) {
        mEditPreferLanguageView = (IEditPreferLanguageView) view;
    }

    @Override
    public void detachView() {
        mEditPreferLanguageView = null;
    }
}
