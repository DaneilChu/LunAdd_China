package com.infinity.lunadd.mvp.view.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.infinity.lunadd.mvp.view.setting.activity.EditPreferLanguageActivity;
import com.jaeger.library.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.infinity.lunadd.R;
import com.infinity.lunadd.base.BaseActivity;
import com.infinity.lunadd.mvp.presenter.login.impl.LoginImpl;
import com.infinity.lunadd.mvp.view.home.activity.MainActivity;
import com.infinity.lunadd.mvp.view.login.ILoginView;
import com.infinity.lunadd.util.PreferenceManager;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
/**
 * Created by DanielChu ou on 2017/6/13.
 */
public class LoginActivity extends BaseActivity implements ILoginView {
    @Inject
    LoginImpl mLoginImpl;
    @BindView(R.id.name_input)
    TextInputLayout name_input;
    @BindView(R.id.linear_layout_register)
    LinearLayout registerView;
    @BindView(R.id.textview_preferlanguage)
    TextView preferlanguage;

    static final int CHANGE_PREFERLANGUAGE = 1;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.init(this.getClass().getSimpleName());
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void initData() {
        mLoginImpl.showPreferLanguage();
    }

    @Override
    public void initDagger() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initContentView() {
        setContentView(R.layout.activity_login);
    }


    @Override
    public void initViewsAndListener() {
        mLoginImpl.isRegisterViewVisable();
        mLoginImpl.doingSplash();
        EMClient.getInstance().chatManager().loadAllConversations();
    }

    @Override
    public void initPresenter() {
        mLoginImpl.attachView(this);
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        mLoginImpl.detachView();
    }


    @Override
    public void initToolbar() {

    }


    @Override
    public void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_change_preferlanguage)
    @Override
    public void toEditPreferLanguage(){
        Intent intent = new Intent(this, EditPreferLanguageActivity.class);
        startActivityForResult(intent, CHANGE_PREFERLANGUAGE);
    }


    @Override
    public boolean isLoginViewShowing() {

        return registerView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void showAuthenticating(){
        showProgress(getString(R.string.authenticating));
    }

    @Override
    public void showRegisterCompleted(){
        showToast(getString(R.string.register_complete));
    }

    @Override
    public void showRegisterFailed(Throwable e){
        showToast(getString(R.string.register_fail) + e.getMessage());
    }

    @Override
    public void showPermissionRejected(){
        showToast(getString(R.string.permission_rejected));
    }

    @Override
    public void showLoginFailed(Throwable e){
        showToast(getString(R.string.login_fail) + e.getMessage());
    }

    @Override
    public void showRegisterView() {
        registerView.setVisibility(LinearLayout.VISIBLE);
    }

    @Override
    public void dismissRegisterView() {
        registerView.setVisibility(LinearLayout.GONE);
    }

    @OnClick(R.id.btn_login)
    public void submit() {
        if (!isLoginViewShowing()) {
            showRegisterView();
        } else {
            mLoginImpl.Register(name_input.getEditText().getText().toString());
        }
    }

    @Override
    public void showPreferLanguage(List<String> preferLanguage){
        String pre = TextUtils.join(",",preferLanguage);
        this.preferlanguage.setText(pre);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case CHANGE_PREFERLANGUAGE:
                    initData();
                    break;
            }
        }

    }

}
