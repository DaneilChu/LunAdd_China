package com.infinity.lunadd.mvp.view.home.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.TextView;

import com.hyphenate.easeui.domain.UserDao;
import com.infinity.lunadd.base.BaseFragment;
import com.infinity.lunadd.R;
import com.infinity.lunadd.mvp.presenter.home.impl.ProfilePresenterImpl;
import com.infinity.lunadd.mvp.view.home.IProfileView;
import com.infinity.lunadd.mvp.view.setting.activity.EditAlarmActivity;
import com.infinity.lunadd.mvp.view.setting.activity.EditLanguageActivity;
import com.infinity.lunadd.mvp.view.setting.activity.EditNameActivity;
import com.infinity.lunadd.mvp.view.setting.activity.EditPreferLanguageActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by DanielChu on 2017/6/9.
 */
public class ProfileFragment extends BaseFragment implements IProfileView {
    @Inject
    ProfilePresenterImpl mProfilePresenterImpl;
    @BindView(R.id.textview_username)
    TextView username;
    @BindView(R.id.textview_preferlanguage)
    TextView preferlanguage;

    static final int CHANGE_USERNAME = 0;
    static final int CHANGE_PREFERLANGUAGE = 1;
    static final int CHANGE_LANGUAGE = 2;
    static final int CHANGE_ALARM = 3;


    @Override
    public void initData() {
        mProfilePresenterImpl.initData();
    }

    @Override
    public void initViews() {

    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }


    @Override
    public int getLayoutRes() {
        return R.layout.fragment_profile;
    }

    @Override
    public void initDagger() {
        mFragmentComponent.inject(this);
    }



    @Override
    public void initPresenter() {
        mProfilePresenterImpl.attachView(this);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mProfilePresenterImpl.detachView();
    }

    @OnClick(R.id.btn_change_username)
    @Override
    public void toEditName(){
        Intent intent = new Intent(getActivity(), EditNameActivity.class);
        startActivityForResult(intent, CHANGE_USERNAME);

    }

    @OnClick(R.id.btn_tolanguage)
    @Override
    public void toLanguage(){
        Intent intent = new Intent(getActivity(), EditLanguageActivity.class);
        startActivity(intent);

    }

    @OnClick(R.id.btn_toalarm)
    @Override
    public void toAlarm(){
        Intent intent = new Intent(getActivity(), EditAlarmActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_change_preferlanguage)
    @Override
    public void toPreferLanguage(){
        Intent intent = new Intent(getActivity(), EditPreferLanguageActivity.class);
        startActivityForResult(intent, CHANGE_PREFERLANGUAGE);
    }

    @Override
    public void showName(String name){
        this.username.setText(name);
    }

    @Override
    public void showPreferLanguage(List<String> preferLanguage){
        String pre = TextUtils.join(",",preferLanguage);
        this.preferlanguage.setText(pre);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK){
            switch (requestCode){
                case CHANGE_USERNAME:
                    initData();
                    break;
                case CHANGE_PREFERLANGUAGE:
                    initData();
                    break;
            }
        }

    }


}
