package com.infinity.lunadd.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.infinity.lunadd.mvp.model.bean.Post;

import java.util.List;

/**
 * Created by Administrator on 2016/4/24.
 */
public class PostEvent {

    public Post mPost;

    public List<Post> mlist;

    public int mAction;

    public PostEvent(@Nullable Post post, @Nullable List<Post> list, @NonNull int action) {
        this.mPost = post;
        this.mAction = action;
        this.mlist = list;
    }

}
