package com.infinity.lunadd.mvp.view.login;

import android.support.design.widget.TextInputLayout;
import android.widget.TextView;

import com.infinity.lunadd.base.BaseView;

import java.util.List;

/**
 * Created by DanielChu on 2017/6/8.
 */
public interface ILoginView extends BaseView {
    void toMainActivity();

    void toEditPreferLanguage();

    boolean isLoginViewShowing();

    void showRegisterView();

    void showPreferLanguage(List<String> preferLanguage);

    void dismissRegisterView();

    void showAuthenticating();

    void showPermissionRejected();

    void showRegisterCompleted();

    void showRegisterFailed(Throwable e);

    void showLoginFailed(Throwable e);



}
