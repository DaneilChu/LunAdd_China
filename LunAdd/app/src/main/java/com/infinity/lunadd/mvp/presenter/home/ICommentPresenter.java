package com.infinity.lunadd.mvp.presenter.home;

import com.infinity.lunadd.base.BasePresenter;
import com.infinity.lunadd.mvp.model.bean.Comment;
import com.infinity.lunadd.mvp.model.bean.Post;

/**
 * Created by DanielChu on 2017/7/24.
 */
public interface ICommentPresenter extends BasePresenter {

    void fetchAllComments(Post post, int page, int size);

    void Comment(Comment comment, int position);

    void deleteComment(Comment comment);

}
