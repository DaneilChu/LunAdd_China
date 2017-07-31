package com.infinity.lunadd.mvp.view.setting.activity;

import android.graphics.Color;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.infinity.lunadd.R;
import com.infinity.lunadd.base.BaseActivity;
import com.infinity.lunadd.mvp.presenter.setting.impl.EditAlarmImpl;
import com.infinity.lunadd.mvp.presenter.setting.impl.EditPreferLanguageImpl;
import com.infinity.lunadd.mvp.view.setting.IEditAlarmView;
import com.infinity.lunadd.mvp.view.setting.IEditPreferLanguageView;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;



/**
 * Created by DanielChu on 2017/6/10.
 */
public class EditAlarmActivity extends BaseActivity implements IEditAlarmView {
    @Inject
    EditAlarmImpl mEditAlarmImpl;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.switch_msg)
    SwitchCompat switch_msg;
    @BindView(R.id.switch_song)
    SwitchCompat switch_song;
    @BindView(R.id.switch_vibrate)
    SwitchCompat switch_vibrate;

    @Override
    protected void initData() {
        mEditAlarmImpl.initData();
    }


    @Override
    public void initDagger() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initContentView() {
        setContentView(R.layout.activity_editalarm);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void initViewsAndListener() {
    }

    @Override
    public void initPresenter() {
        mEditAlarmImpl.attachView(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mEditAlarmImpl.detachView();
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
    public void setResultcode() {
        setResult(RESULT_OK);
    }

    @Override
    public void setSwitch_msg(boolean ismsg) {
        switch_msg.setChecked(ismsg);
    }

    @Override
    public void setSwitch_song(boolean issong) {
        switch_song.setChecked(issong);
    }

    @Override
    public void setSwitch_vibrate(boolean isvibrate) {
        switch_vibrate.setChecked(isvibrate);
    }

    @OnClick(R.id.switch_msg)
    public void changemsg(){
        mEditAlarmImpl.changemsg(switch_msg.isChecked());
    }

    @OnClick(R.id.switch_song)
    public void changesong(){
        mEditAlarmImpl.changesong(switch_song.isChecked());
    }

    @OnClick(R.id.switch_vibrate)
    public void changevibrate(){
        mEditAlarmImpl.changevibrate(switch_vibrate.isChecked());
    }



}
