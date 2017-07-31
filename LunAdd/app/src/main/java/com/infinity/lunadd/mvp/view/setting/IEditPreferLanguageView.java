package com.infinity.lunadd.mvp.view.setting;


import com.infinity.lunadd.base.BaseView;

import java.util.List;

/**
 * Created by DanielChu on 2017/6/8.
 */
public interface IEditPreferLanguageView extends BaseView {


    void showSelectedLanguage(List<String> position);

    void setResultcode();

    void showEmptyListMessage();

}
