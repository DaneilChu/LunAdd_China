package com.infinity.lunadd.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.infinity.lunadd.LunApplication;
import com.infinity.lunadd.R;
import com.infinity.lunadd.di.component.ActivityComponent;
import com.infinity.lunadd.di.component.DaggerActivityComponent;
import com.infinity.lunadd.di.module.ActivityModule;
import com.infinity.lunadd.util.LanguageUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by DanielChu ou on 2017/6/6.
 * BaseAvtivity
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseView {
    public SweetAlertDialog mProgressDialog;
    public ActivityComponent mActivityComponent;
    private Unbinder unbinder;

    public BaseActivity() {
        LanguageUtil.updateConfig(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityComponent = DaggerActivityComponent.builder().activityModule(new ActivityModule(this)).applicationComponent(LunApplication.getAppComponent()).build();
        initContentView();
        unbinder = ButterKnife.bind(this);
        initDagger();
        initPresenter();
        initToolbar();
        initViewsAndListener();
        initData();
    }

    protected abstract void initData();

    public abstract void initDagger();

    public abstract void initContentView();

    public abstract void initViewsAndListener();

    public abstract void initPresenter();

    public abstract void initToolbar();


    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);

    }

    @Override
    public void showProgress(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mProgressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.setTitleText(message);
        mProgressDialog.show();

    }



    @Override
    public void showProgress(String message, int progress) {
        mProgressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mProgressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        mProgressDialog.setTitleText(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.getProgressHelper().setProgress(progress);
        mProgressDialog.show();

    }

    @Override
    public void showToast(String msg) {
        if (!isFinishing())
            Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

    }

    @Override
    public void showUpdating() {
        showProgress(getString(R.string.updating));
    }

    @Override
    public void showError() {
        mProgressDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        mProgressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        mProgressDialog.setTitleText(getString(R.string.error));
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    @Override
    public void showLoading(){
        showProgress(getString(R.string.loading));
    }


    @Override
    public void close() {
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

    }


    public void showWarningDialog(String title, String content, SweetAlertDialog.OnSweetClickListener listener) {
        SweetAlertDialog mWarningDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setConfirmText(getString(R.string.confirm))
                .setCancelText(getString(R.string.cancel))
                .setTitleText(title)
                .setContentText(content)
                .setConfirmClickListener(listener);
        mWarningDialog.show();
    }

    public void showErrorDialog(String title, String content, SweetAlertDialog.OnSweetClickListener listener) {
        SweetAlertDialog mErrorDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setConfirmText(getString(R.string.confirm))
                .setTitleText(title)
                .setContentText(content)
                .setConfirmClickListener(listener);
        mErrorDialog.show();
    }
}
