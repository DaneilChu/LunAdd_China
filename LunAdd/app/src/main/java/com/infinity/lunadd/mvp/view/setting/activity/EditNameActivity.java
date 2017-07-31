package com.infinity.lunadd.mvp.view.setting.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.infinity.lunadd.R;
import com.infinity.lunadd.base.BaseActivity;
import com.infinity.lunadd.mvp.presenter.setting.impl.EditNameImpl;
import com.infinity.lunadd.mvp.view.setting.IEditNameView;
import com.jaeger.library.StatusBarUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;



/**
 * Created by DanielChu on 2017/6/10.
 */
public class EditNameActivity extends BaseActivity implements IEditNameView {
    @Inject
    EditNameImpl mEditNameImpl;
    @BindView(R.id.name_input)
    TextInputLayout username;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void initData() {
        mEditNameImpl.initData();
    }


    @Override
    public void initDagger() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initContentView() {
        setContentView(R.layout.activity_editusername);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void initViewsAndListener() {
    }

    @Override
    public void initPresenter() {
        mEditNameImpl.attachView(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mEditNameImpl.detachView();
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

    @OnClick(R.id.btn_confirm)
    public void confirm() {
        if (username.getEditText()!=null) {
            mEditNameImpl.saveName(username.getEditText().getText().toString());
        }

    }



    @OnClick(R.id.btn_cancel)
    public void cancel() {
        close();
    }

    @Override
    public void setResultcode() {
        setResult(RESULT_OK);
    }

    @Override
    public void showEmptyNameMessage() {
        showProgress(getString(R.string.empty_name));
    }


    @Override
    public void showName(String name){
        if (username.getEditText()!=null) {
            this.username.getEditText().setText(name);
        }
    }




}
