package com.infinity.lunadd.mvp.view.home;

import android.content.Intent;

import com.infinity.lunadd.base.BaseView;

/**
 * Created by DanielChu on 2017/7/22.
 */
public interface IAddPostView extends BaseView {

    void showDialog();

    void hideDialog();

    void setResultCode(Intent intent);

    void submit();
}
