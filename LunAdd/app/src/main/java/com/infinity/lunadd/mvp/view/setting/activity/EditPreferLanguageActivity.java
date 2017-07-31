package com.infinity.lunadd.mvp.view.setting.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.infinity.lunadd.R;
import com.infinity.lunadd.base.BaseActivity;
import com.infinity.lunadd.mvp.presenter.setting.impl.EditPreferLanguageImpl;
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
public class EditPreferLanguageActivity extends BaseActivity implements IEditPreferLanguageView {
    @Inject
    EditPreferLanguageImpl mEditPreferLanguageImpl;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.listvew_preferlanguage)
    ListView list_preferlanguage;
    ArrayAdapter<String> language_adapter;

    @Override
    protected void initData() {
        mEditPreferLanguageImpl.initData();
    }


    @Override
    public void initDagger() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initContentView() {
        setContentView(R.layout.activity_editpreferlanguage);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void initViewsAndListener() {
        String[] language_list = getResources().getStringArray(R.array.preferlanguage_array);
        language_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, language_list);
        list_preferlanguage.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list_preferlanguage.setAdapter(language_adapter);
    }

    @Override
    public void initPresenter() {
        mEditPreferLanguageImpl.attachView(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mEditPreferLanguageImpl.detachView();
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
        SparseBooleanArray checked = list_preferlanguage.getCheckedItemPositions();
        ArrayList<String> selectedItems = new ArrayList<>();
        if (checked!=null) {
            for (int i = 0; i < checked.size(); i++) {
                // Item position in adapter
                int position = checked.keyAt(i);
                // Add sport if it is checked i.e.) == TRUE!
                if (checked.valueAt(i))
                    selectedItems.add(language_adapter.getItem(position));
            }
        }
        mEditPreferLanguageImpl.savePreferLanguage(selectedItems);
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
    public void showSelectedLanguage(List<String> preferlanguage){
        if (preferlanguage!=null) {
            for (int i = 0; i < preferlanguage.size(); i++) {
                list_preferlanguage.setItemChecked(language_adapter.getPosition(preferlanguage.get(i)), true);
            }
        }

    }

    @Override
    public void showEmptyListMessage(){
        showProgress(getString(R.string.empty_language_list));
    }


}
