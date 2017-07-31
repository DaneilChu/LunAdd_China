package com.infinity.lunadd.mvp.presenter.home;

import com.infinity.lunadd.base.BasePresenter;

import java.io.File;

/**
 * Created by DanielChu on 2017/7/23.
 */
public interface IAddPostPresenter extends BasePresenter {

    void commit(String content, File file);

}
