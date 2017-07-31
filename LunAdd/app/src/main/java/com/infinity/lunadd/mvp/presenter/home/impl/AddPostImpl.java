package com.infinity.lunadd.mvp.presenter.home.impl;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.hyphenate.easeui.domain.User;
import com.orhanobut.logger.Logger;
import com.infinity.lunadd.base.BaseView;
import com.infinity.lunadd.event.EventConstant;
import com.infinity.lunadd.event.PostEvent;
import com.infinity.lunadd.mvp.model.bean.Post;
import com.infinity.lunadd.mvp.model.rx.RxBus;
import com.infinity.lunadd.mvp.model.rx.RxLeanCloud;
import com.infinity.lunadd.mvp.presenter.home.IAddPostPresenter;
import com.infinity.lunadd.mvp.view.home.IAddPostView;
import com.infinity.lunadd.util.PreferenceManager;

import java.io.File;
import java.io.FileNotFoundException;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by DanielChu on 2017/7/23.
 */
public class AddPostImpl implements IAddPostPresenter {

    private final RxLeanCloud mRxLeanCloud;
    private final PreferenceManager manager;
    private final RxBus mRxBus;
    private IAddPostView mView;

    @Inject
    public AddPostImpl(RxBus mRxbus, RxLeanCloud rxLeanCloud, PreferenceManager preferenceManager) {
        this.mRxLeanCloud = rxLeanCloud;
        this.manager = preferenceManager;
        this.mRxBus = mRxbus;
    }

    @Override
    public void commit(final String content, File file) {
        if (TextUtils.isEmpty(content)) {
            mView.showToast("写点什么吧?");
            return;
        }
        if (file == null) {
            mView.showToast("选张好看的图片吧");
            return;
        }
        mView.showProgress("上传中...");
        AVFile avFile = null;
        try {
            Logger.d("Start Uploading");
            avFile = AVFile.withFile(manager.getCurrentUserId(), file);
            Post post = new Post();
            post.setAuthorId(AVUser.getCurrentUser().getObjectId());
            post.setAuthor(User.getCurrentUser(User.class));
            post.setContent(content);
            post.setPicture(avFile);
            post.setCommentcount(0);
            post.setLanguage(manager.getPreferlanguageLanguage());
            mRxLeanCloud.SavePostByLeanCloud(post)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Post>() {
                        @Override
                        public void onCompleted() {
                            mView.showToast("发布成功");
                            mView.hideProgress();
                            mView.close();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.e(e.getMessage());
                            mView.hideProgress();
                            mView.showToast("上传失败了，请稍后再试吧");
                        }

                        @Override
                        public void onNext(Post post) {
                            mRxBus.post(new PostEvent(post, null, EventConstant.ADD));
                        }
                    });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void attachView(@NonNull BaseView view) {
        mView = (IAddPostView) view;
    }

    @Override
    public void detachView() {

    }
}
