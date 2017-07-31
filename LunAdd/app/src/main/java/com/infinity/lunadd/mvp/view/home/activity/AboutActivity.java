package com.infinity.lunadd.mvp.view.home.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.infinity.lunadd.R;
import com.infinity.lunadd.base.BaseActivity;
import com.infinity.lunadd.mvp.presenter.home.impl.AboutImpl;
import com.infinity.lunadd.mvp.presenter.setting.impl.EditNameImpl;
import com.infinity.lunadd.mvp.view.home.IAboutView;
import com.infinity.lunadd.mvp.view.setting.IEditNameView;
import com.jaeger.library.StatusBarUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by DanielChu on 2017/6/13.
 */
public class AboutActivity extends BaseActivity implements IAboutView {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Inject
    AboutImpl mAboutImpl;
    @BindView(R.id.about_version)
    TextView textview_version;

    @Override
    protected void initData() {

    }


    @Override
    public void initDagger() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initContentView() {
        setContentView(R.layout.activity_about);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void initViewsAndListener() {

    }

    @Override
    public void initPresenter() {
        mAboutImpl.attachView(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mAboutImpl.detachView();
    }

    @Override
    public void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }

    @Override
    public void showCurrentVersion(String version) {
        textview_version.setText(version);
    }


}
