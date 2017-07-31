package com.infinity.lunadd.mvp.presenter.home;

import com.infinity.lunadd.mvp.model.bean.Post;
import com.infinity.lunadd.base.BasePresenter;

/**
 * Created by DanielChu on 2017/7/22.
 */
public interface IWallPresenter extends BasePresenter {


    void LoadingDataFromNet(boolean isReFresh, int size, int page);

    void delete(Post post);


}
