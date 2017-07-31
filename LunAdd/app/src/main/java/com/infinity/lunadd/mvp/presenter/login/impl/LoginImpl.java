package com.infinity.lunadd.mvp.presenter.login.impl;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.domain.UserDao;
import com.infinity.lunadd.mvp.presenter.login.ILoginPresenter;
import com.infinity.lunadd.mvp.view.login.ILoginView;
import com.orhanobut.logger.Logger;
import com.infinity.lunadd.base.BaseView;
import com.infinity.lunadd.di.scope.ContextLife;
import com.infinity.lunadd.mvp.model.rx.RxLeanCloud;
import com.infinity.lunadd.util.PreferenceManager;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.UUID;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by Daniel on 2017/6/9.
 */
public class LoginImpl implements ILoginPresenter, Handler.Callback {

    ILoginView mLoginView;
    private final Context mContext;
    private final PreferenceManager mPreferenceManager;

    private final RxLeanCloud mRxleanCloud;
    private Handler mHandler;
    private static final int MESSAGE_WHAT = 1;

    @Inject
    public LoginImpl(@ContextLife("Activity") Context context, PreferenceManager preferenceManager, RxLeanCloud mRxleanCloud) {
        this.mRxleanCloud = mRxleanCloud;
        mContext = context;
        mPreferenceManager = preferenceManager;

    }

    @Override
    public void onActivityStart() {
        if (mHandler != null && !mHandler.hasMessages(MESSAGE_WHAT)) {
            mHandler.sendEmptyMessage(MESSAGE_WHAT);
        }

    }

    @Override
    public void onActivityPause() {
        if (mHandler != null && mHandler.hasMessages(MESSAGE_WHAT)) {
            mHandler.removeMessages(MESSAGE_WHAT);
        }
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public void Register(final String username) {


        mLoginView.showAuthenticating();
        //TODO: the userid should be changed to installation id
        final String userid = UUID.randomUUID().toString().replaceAll("-", "");
        final String passwd = UUID.randomUUID().toString().replaceAll("-", "");
        Observable.zip(mRxleanCloud.HXRegister(userid, passwd), mRxleanCloud.Register(userid, passwd, username,mPreferenceManager.getPreferlanguageLanguage()), new Func2<Boolean, User, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean, User user) {
                return (aBoolean && user != null);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        mLoginView.showRegisterCompleted();
                        mPreferenceManager.saveCurrentUserId(userid);
                        mPreferenceManager.saveCurrentUserPassword(passwd);
                        mPreferenceManager.saveCurrentUserName(username);
                        mLoginView.hideProgress();
                        Login();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginView.showRegisterFailed(e);
                        mLoginView.hideProgress();
                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            Logger.d("注册成功");
                        }
                    }
                });

    }



    @SuppressWarnings("ConstantConditions")
    @Override
    public void Login() {
        final String userid = mPreferenceManager.getCurrentUserId();
        final String passwd = mPreferenceManager.getCurrentUserPassword();
        mLoginView.showLoading();
        mRxleanCloud.Login(userid, passwd)
                .flatMap(new Func1<AVUser, Observable<AVUser>>() {
                    @Override
                    public Observable<AVUser> call(AVUser user) {
                        //TODO: SaveInstallationId should be removed
                        return Observable.zip(mRxleanCloud.SaveInstallationId(), mRxleanCloud.GetUserByUserid(user.getString(UserDao.OTHERUSERID)), new Func2<String, AVUser, AVUser>() {
                            @Override
                            public AVUser call(String s, AVUser user) {
                                Logger.e(s);
                                AVUser u = AVUser.getCurrentUser();
                                u.put(UserDao.INSTALLATIONID,s);
                                if (user != null) {
                                    Logger.e(user.getString(UserDao.INSTALLATIONID));
                                    u.put(UserDao.OTHERUSERINSTALLATIONID,user.getString(UserDao.INSTALLATIONID));
                                    u.put(UserDao.OTHERUSERNAME,user.getString(UserDao.NICK));
                                }
                                return u;
                            }

                        });
                    }
                })
                .flatMap(new Func1<AVUser, Observable<AVUser>>() {
                    @Override
                    public Observable<AVUser> call(AVUser user) {
                        return mRxleanCloud.SaveUserByLeanCloud(user);
                    }
                }).flatMap(new Func1<AVUser, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(AVUser user) {
                if (user != null) {
                    return mRxleanCloud.HXLogin(userid, passwd);
                } else {
                    return Observable.error(new Throwable("Login Fail"));
                }
            }
        }).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                mLoginView.hideProgress();
                mPreferenceManager.setIslogin(true);
                mLoginView.toMainActivity();
            }

            @Override
            public void onError(Throwable e) {
                mLoginView.showLoginFailed(e);
                Logger.e(e.getMessage());
                mLoginView.hideProgress();
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    Logger.d("Login successful");
                }
            }
        });

    }

/*
    @Override
    public void Login() {
        final String username = mPreferenceManager.getCurrentUserId();
        final String password = mPreferenceManager.getCurrentUserPassword();
        mLoginView.showLoading();
        mRxleanCloud.Login(username, password)
                .subscribe(new Observer<AVUser>() {
                    @Override
                    public void onCompleted() {
                        mLoginView.hideProgress();
                        mPreferenceManager.setIslogin(true);
                        mLoginView.toMainActivity();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginView.showLoginFailed(e);
                        Logger.e(e.getMessage());
                        mLoginView.hideProgress();
                    }

                    @Override
                    public void onNext(AVUser user) {
                        if (user!=null) {
                            Logger.d("Login successful");
                        }
                    }
                });

    }
*/

    @Override
    public void isRegisterViewVisable() {
        mHandler = new Handler(this);
        if (mPreferenceManager.isLogined()) {
            mLoginView.dismissRegisterView();
        } else {
            mLoginView.showRegisterView();
        }
    }

    @Override
    public void doingSplash() {
        if (mPreferenceManager.isFirstTime()) {
            RxPermissions.getInstance(mContext)
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (!aBoolean) {
                                mLoginView.showPermissionRejected();
                                mLoginView.close();
                            } else {
                                mPreferenceManager.saveFirsttime(false);
                            }
                        }
                    });
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, 3000);
        }
    }

    @Override
    public void attachView(@NonNull BaseView view) {
        mLoginView = (ILoginView) view;

    }

    @Override
    public void detachView() {

    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MESSAGE_WHAT) {
            if (mPreferenceManager.isLogined() && AVUser.getCurrentUser() != null) {
               mLoginView.toMainActivity();
                //TODO: What should I do?
            //    Login();
            }else if (mPreferenceManager.getCurrentUserId()!=null && mPreferenceManager.getCurrentUserPassword()!=null){
                Login();
            }
        }

        return false;
    }

    @Override
    public void showPreferLanguage(){
        if (mPreferenceManager.getPreferlanguageLanguage() != null) {
            mLoginView.showPreferLanguage(mPreferenceManager.getPreferlanguageLanguage());
        }
    }
}
