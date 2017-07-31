package com.infinity.lunadd.mvp.view.home;


import com.infinity.lunadd.base.BaseView;

import java.util.List;

/**
 * Created by DanielChu on 2017/6/8.
 */
public interface IProfileView extends BaseView {


    void toEditName();

    void toLanguage();

    void toAlarm();

    void toPreferLanguage();

    void showName(String username);

    void showPreferLanguage(List<String> preferLanguage);


}
