package com.infinity.lunadd.mvp.presenter.home;


import android.support.annotation.Nullable;

import com.hyphenate.easeui.domain.User;
import com.infinity.lunadd.base.BasePresenter;

/**
 * Created by DanielChu on 2017/6/8.
 */
public interface ILunAddPresenter extends BasePresenter {

    void commit(int duration, String content);

    void initData();

    void removePeople();

    void initTime();

    void loadMessage();

    void removeHandler();

}
