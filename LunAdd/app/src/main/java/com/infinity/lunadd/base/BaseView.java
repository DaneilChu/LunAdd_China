package com.infinity.lunadd.base;

import com.infinity.lunadd.R;

/**
 * Created by Administrator on 2016/3/25.
 */
public interface BaseView {

    void showProgress(String message);

    void showProgress(String message, int progress);

    void hideProgress();

    void showToast(String message);

    void close();

    void showUpdating();

    void showError();

    void showLoading();

}
