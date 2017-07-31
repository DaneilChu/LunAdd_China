package com.infinity.lunadd.mvp.presenter.setting.impl;


import android.support.annotation.NonNull;

import com.avos.avoscloud.AVUser;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.domain.UserDao;
import com.infinity.lunadd.base.BaseView;
import com.infinity.lunadd.mvp.model.rx.RxLeanCloud;
import com.infinity.lunadd.mvp.presenter.setting.IEditNamePresenter;
import com.infinity.lunadd.mvp.view.setting.IEditNameView;
import com.infinity.lunadd.util.PreferenceManager;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by DanielChu on 2017/6/9.
 */
public class EditNameImpl implements IEditNamePresenter {

    private final PreferenceManager mPreferenceManager;
    private IEditNameView mEditNameView;
    private final RxLeanCloud mRxLeanCloud;

    @Inject
    public EditNameImpl(RxLeanCloud rxLeanCloud, PreferenceManager preferenceManager) {
        mPreferenceManager = preferenceManager;
        mRxLeanCloud = rxLeanCloud;
        Logger.init(this.getClass().getSimpleName());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void initData() {
        if (mPreferenceManager.getCurrentUserName() != null) {
            mEditNameView.showName(mPreferenceManager.getCurrentUserName());
        }
    }


    @Override
    public void saveName(final String username) {
        mEditNameView.showUpdating();
         AVUser avUser = AVUser.getCurrentUser();
        if (username == null || username.equals("")) {
            mEditNameView.showEmptyNameMessage();
        }else{
            avUser.put(UserDao.NICK,username);
            mRxLeanCloud.SaveUserByLeanCloud(avUser)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<AVUser>() {
                        @Override
                        public void onCompleted() {
                            mEditNameView.hideProgress();
                            mPreferenceManager.saveCurrentUserName(username);
                            mEditNameView.close();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mEditNameView.showError();
                        }

                        @Override
                        public void onNext(AVUser user) {
                            if (user != null) {
                                mEditNameView.setResultcode();
                            }
                        }
                    });
        }
    }

    @Override
    public void attachView(@NonNull BaseView view) {
        mEditNameView = (IEditNameView) view;
    }

    @Override
    public void detachView() {
        mEditNameView = null;
    }
}
