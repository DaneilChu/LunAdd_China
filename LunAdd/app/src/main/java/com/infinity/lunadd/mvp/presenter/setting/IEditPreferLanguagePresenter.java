package com.infinity.lunadd.mvp.presenter.setting;

import com.infinity.lunadd.base.BasePresenter;

import java.util.List;

/**
 * Created by DanielChu on 2017/6/8.
 */
public interface IEditPreferLanguagePresenter extends BasePresenter {

    void savePreferLanguage(List<String> preferlanguage);

    void initData();

}
