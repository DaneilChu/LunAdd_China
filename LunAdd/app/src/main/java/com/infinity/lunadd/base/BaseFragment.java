package com.infinity.lunadd.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infinity.lunadd.R;
import com.infinity.lunadd.di.component.DaggerFragmentComponent;
import com.infinity.lunadd.di.component.FragmentComponent;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by DanielChu on 2017/6/7.
 */
public abstract class BaseFragment extends Fragment implements BaseView {
    private BaseActivity mActivity;
    public FragmentComponent mFragmentComponent;
    protected BasePresenter mPresenter;
    private Unbinder unbinder;

    /**
     * 初始化布局
     */
    public abstract int getLayoutRes();

    /**
     * 初始化Views
     */
    public abstract void initViews();

    public abstract void initDagger();

    public abstract void initData();

    public abstract void initPresenter();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 获取Fragment布局文件的View
     *
     * @param inflater
     * @param container
     * @return
     */
    private View getCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(getLayoutRes(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mFragmentComponent = DaggerFragmentComponent
                .builder()
                .activityComponent(getBaseActivity().mActivityComponent)
                .build();
        initDagger();
        initViews();
        initPresenter();
        initData();
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layoutView = getCreateView(inflater, container);
        unbinder = ButterKnife.bind(this, layoutView);
        return layoutView;
    }

    /**
     * 获取当前Fragment状态
     *
     * @return true为正常 false为未加载或正在删除
     */
    private boolean getStatus() {
        return (isAdded() && !isRemoving());
    }

    @Override
    public void showProgress(String message) {
        if (getStatus() && getBaseActivity() != null) {
            getBaseActivity().showProgress(message);
        }
    }

    @Override
    public void showProgress(String message, int progress) {
        if (getStatus() && getBaseActivity() != null) {
            getBaseActivity().showProgress(message, progress);
        }
    }

    @Override
    public void hideProgress() {
        if (getStatus() && getBaseActivity() != null) {

            getBaseActivity().hideProgress();
        }

    }

    @Override
    public void showUpdating() {
        showProgress(getString(R.string.updating));
    }

    @Override
    public void showError() {
        showProgress(getString(R.string.error));
    }

    @Override
    public void showToast(String message) {
        if (getStatus() && getBaseActivity() != null) {
            getBaseActivity().showToast(message);

        }
    }

    @Override
    public void showLoading(){
        showProgress(getString(R.string.loading));
    }


    @Override
    public void close() {

    }

    public void showWarningDialog(String content, String title, SweetAlertDialog.OnSweetClickListener listener) {
        getBaseActivity().showWarningDialog(title, content, listener);
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    public BaseActivity getBaseActivity() {
        if (mActivity == null) {
            mActivity = (BaseActivity) getActivity();
        }
        return mActivity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}