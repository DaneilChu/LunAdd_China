package com.infinity.lunadd.event;

import android.support.annotation.Nullable;

import com.infinity.lunadd.mvp.model.bean.Comment;

import java.util.List;

/**
 * Created by DanielChu on 2017/7/24.
 */
public class CommentsEvent {
    public List<Comment> mCommentList;
    public Comment mComment;
    public int mAction;

    public CommentsEvent(@Nullable Comment comment, @Nullable List<Comment> commentList, int action) {
        mComment = comment;
        mCommentList = commentList;
        mAction = action;
    }
}
