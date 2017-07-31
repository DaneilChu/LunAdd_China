package com.infinity.lunadd.mvp.presenter.home.impl;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.infinity.lunadd.base.BaseView;
import com.infinity.lunadd.event.EventConstant;
import com.infinity.lunadd.event.PostEvent;
import com.infinity.lunadd.mvp.model.bean.Post;
import com.infinity.lunadd.mvp.model.rx.RxBus;
import com.infinity.lunadd.mvp.model.rx.RxLeanCloud;
import com.infinity.lunadd.mvp.presenter.home.IWallPresenter;
import com.infinity.lunadd.mvp.view.home.IWallView;
import com.infinity.lunadd.util.PreferenceManager;
import com.infinity.lunadd.util.SchedulersCompat;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;

/**
 * Created by Roger on 2016/4/20.
 */
public class WallPresenterImpl implements IWallPresenter {

    private final RxLeanCloud mRxLeanCloud;
    private final RxBus mRxBus;
    private final PreferenceManager mPreferenceManager;
    private IWallView mSouvenirView;
    private Subscription getAllSouvenir;

    @Inject
    public WallPresenterImpl(RxLeanCloud rxLeanCloud, PreferenceManager preferenceManager, RxBus rxbus) {
        mRxBus = rxbus;
        mRxLeanCloud = rxLeanCloud;
        mPreferenceManager = preferenceManager;
        Logger.init(this.getClass().getSimpleName());
    }


    @Override
    public void LoadingDataFromNet(final boolean isFresh, final int size, final int page) {
        mSouvenirView.showRefreshingLoading();
        getAllSouvenir = mRxLeanCloud.GetALlPostByLeanCloud(mPreferenceManager.getPreferlanguageLanguage(), size, page)
                .compose(SchedulersCompat.<List<Post>>applyIoSchedulers())
                .subscribe(new Observer<List<Post>>() {
                    @Override
                    public void onCompleted() {
                        mSouvenirView.hideRefreshingLoading();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        mSouvenirView.hideRefreshingLoading();
                        mSouvenirView.showToast("可能出了点错误哦");
                    }

                    @Override
                    public void onNext(List<Post> list) {
                        if (page == 0) {
                            mRxBus.post(new PostEvent(null, list, EventConstant.REFRESH));
                        } else {
                            mRxBus.post(new PostEvent(null, list, EventConstant.LOADMORE));
                        }

                    }
                });


    }

    @Override
    public void delete(final Post post) {
        mRxLeanCloud.delete(post)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mSouvenirView.showProgress("正在删除Moment...");
                    }
                }).compose(SchedulersCompat.<Boolean>observeOnMainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        mSouvenirView.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSouvenirView.hideProgress();
                        mSouvenirView.showToast("出错啦");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        mSouvenirView.showToast("删除成功");
                        mRxBus.post(new PostEvent(post, null, EventConstant.DELETE));
                    }
                });
    }


    @Override
    public void attachView(@NonNull BaseView view) {
        mSouvenirView = (IWallView) view;
    }

    @Override
    public void detachView() {
        if (getAllSouvenir != null && !getAllSouvenir.isUnsubscribed()) {
            getAllSouvenir.unsubscribe();
        }
    }
}
