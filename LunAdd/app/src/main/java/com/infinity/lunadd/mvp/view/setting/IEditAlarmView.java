package com.infinity.lunadd.mvp.view.setting;


import com.infinity.lunadd.base.BaseView;

/**
 * Created by DanielChu on 2017/6/8.
 */
public interface IEditAlarmView extends BaseView {

    void setSwitch_msg(boolean ismsg);

    void setSwitch_song(boolean issong);

    void setSwitch_vibrate(boolean isvibrate);

    void setResultcode();

}
