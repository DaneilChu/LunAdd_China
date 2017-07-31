package com.infinity.lunadd.mvp.view.home;


import android.app.Fragment;

import com.infinity.lunadd.base.BaseView;

/**
 * Created by DanielChu on 2017/6/8.
 */
public interface IMainView extends BaseView {

/*
    void replaceFragment(Fragment to, String tag, boolean isExpanded);

    void fabOnclick();

    boolean isHadLover();

    void initData(ImageView avatar, TextView nick, ImageView ivAlbum);

    void UploadPic(Uri uri);

    void Share();

    void Logout();
*/

    void updateUnreadLabel();

}
