package com.infinity.lunadd.mvp.presenter.setting;


import android.support.design.widget.TextInputLayout;

import com.infinity.lunadd.base.BasePresenter;

/**
 * Created by DanielChu on 2017/6/8.
 */
public interface IEditNamePresenter extends BasePresenter {

    void saveName(String username);

    void initData();

}
