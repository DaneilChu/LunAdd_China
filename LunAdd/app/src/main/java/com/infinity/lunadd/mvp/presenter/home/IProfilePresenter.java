package com.infinity.lunadd.mvp.presenter.home;

import com.hyphenate.easeui.domain.User;
import com.infinity.lunadd.base.BasePresenter;

/**
 * Created by DanielChu on 2017/6/8.
 */
public interface IProfilePresenter extends BasePresenter {

    void onError();

    void initData();

}
