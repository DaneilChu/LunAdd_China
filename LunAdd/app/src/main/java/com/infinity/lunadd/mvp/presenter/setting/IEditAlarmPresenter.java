package com.infinity.lunadd.mvp.presenter.setting;


import android.widget.ToggleButton;

import com.infinity.lunadd.base.BasePresenter;

/**
 * Created by DanielChu on 2017/6/8.
 */
public interface IEditAlarmPresenter extends BasePresenter {

    void changemsg(boolean ismsg);

    void changesong(boolean issong);

    void changevibrate(boolean isvibrate);

    void initData();

}
