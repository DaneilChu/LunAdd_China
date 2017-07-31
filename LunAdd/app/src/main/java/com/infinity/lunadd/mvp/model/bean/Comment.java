package com.infinity.lunadd.mvp.model.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.hyphenate.easeui.domain.User;
import com.infinity.lunadd.mvp.model.dao.CommentDao;

/**
 * Created by DanielChu on 2017/7/24.
 */
@AVClassName("Comment")
public class Comment extends AVObject implements Cloneable {

    private User auhtor;
    private User reply;
    private String mComment;
    private Post mPost;

    public Post getPost() {
        return (Post) get(CommentDao.POST);
    }

    public void setPost(Post post) {
        put(CommentDao.POST, post);
    }

    public User getAuhtor() {
        return (User) get(CommentDao.AUTHOR);
    }

    public void setAuhtor(User auhtor) {
        put(CommentDao.AUTHOR, auhtor);
    }

    public User getReply() {
        return (User) get(CommentDao.REPLY_TO);
    }

    public void setReply(User replyTo) {
        put(CommentDao.REPLY_TO, replyTo);
    }

    public String getComment() {
        return getString(CommentDao.COMMENT);
    }

    public void setComment(String comment) {
        put(CommentDao.COMMENT, comment);
    }

    @Override
    public Comment clone() {
        try {
            return (Comment) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
