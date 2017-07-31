package com.infinity.lunadd.mvp.view.home;

import com.infinity.lunadd.base.BaseView;

/**
 * Created by Administrator on 2016/4/20.
 */
public interface IWallView extends BaseView {

    void showRefreshingLoading();

    void hideRefreshingLoading();

    void toAddPostActivity();


}
