package com.infinity.lunadd.mvp.presenter.login;

import android.support.design.widget.TextInputLayout;
import android.widget.TextView;

import com.infinity.lunadd.base.BasePresenter;

import java.util.List;

/**
 * Created by DanielChu on 2017/6/8.
 */
public interface ILoginPresenter extends BasePresenter {
    void onActivityStart();

    void onActivityPause();

    void Register(String username);

    void Login();

    void doingSplash();

    void showPreferLanguage();

    void isRegisterViewVisable();


}
