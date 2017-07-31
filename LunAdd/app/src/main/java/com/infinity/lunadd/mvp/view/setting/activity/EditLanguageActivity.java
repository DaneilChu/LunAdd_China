package com.infinity.lunadd.mvp.view.setting.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.infinity.lunadd.R;
import com.infinity.lunadd.base.BaseActivity;
import com.infinity.lunadd.mvp.presenter.setting.impl.EditLanguageImpl;
import com.infinity.lunadd.mvp.view.home.activity.MainActivity;
import com.infinity.lunadd.mvp.view.setting.IEditLanguageView;
import com.jaeger.library.StatusBarUtil;


import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;



/**
 * Created by DanielChu on 2017/6/10.
 */
public class EditLanguageActivity extends BaseActivity implements IEditLanguageView {
    @Inject
    EditLanguageImpl mEditLanguageImpl;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.listvew_language)
    ListView list_language;
    ArrayAdapter<String> language_adapter;

    @Override
    protected void initData() {
    }


    @Override
    public void initDagger() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initContentView() {
        setContentView(R.layout.activity_editlanguage);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void initViewsAndListener() {
        String[] language_list = getResources().getStringArray(R.array.language_array);
        language_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, language_list);
        list_language.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list_language.setAdapter(language_adapter);
    }

    @Override
    public void initPresenter() {
        mEditLanguageImpl.attachView(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mEditLanguageImpl.detachView();
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
        int checked = list_language.getCheckedItemPosition()+1;
        mEditLanguageImpl.saveLanguage(checked);
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
    public void toMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // 殺掉進程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @Override
    public void showEmptyListMessage(){
        showProgress(getString(R.string.empty_language_list));
    }

    public void changeLanguage(String language){

    }


}
