package com.infinity.lunadd.mvp.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.hyphenate.easeui.domain.User;
import com.infinity.lunadd.mvp.model.dao.PostDao;
import com.orhanobut.logger.Logger;

import java.util.List;


/**
 * Created by DanielChu on 2017/7/21.
 */
@AVClassName("Post")
public class Post extends AVObject implements Parcelable {
    private String Content;
    private String Picture;
    private User Author;
    private boolean IsLikedMine;
    private boolean IsLikedOther;
    private String AuthorId;
    private String ohterUserId;
    private int commentcount;

    public int getCommentcount() {
        return getInt(PostDao.POST_COMMENT_COUNT);
    }

    public void setCommentcount(int commentcount) {
        put(PostDao.POST_COMMENT_COUNT, commentcount);
    }


    public Post() {
        super();

    }

    protected Post(Parcel in) {
        super(in);
    }

    public static final Creator<Post> CREATOR = AVObjectCreator.instance;

    public String getContent() {
        return getString(PostDao.POST_CONTENT);
    }

    public void setContent(String content) {
        put(PostDao.POST_CONTENT, content);
    }


    public String getPicture() {
        return ((AVFile) get(PostDao.POST_PICTURE)).getUrl();
    }

    public void setPicture(AVFile picture) {
        put(PostDao.POST_PICTURE, picture);
    }

    public AVUser getAuthor() {
        return (AVUser) get(PostDao.POST_AUTHOR);
    }

    public void setAuthor(AVUser author) {
        put(PostDao.POST_AUTHOR, author);
    }


    public String getAuthorId() {
        return getString(PostDao.POST_AUTHOR_ID);
    }

    public void setAuthorId(String authorId) {
        put(PostDao.POST_AUTHOR_ID, authorId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        return objectId.equals(post.objectId);

    }

    @Override
    public int hashCode() {
        return objectId.hashCode();
    }


    public List<String> getLanguage() {
        return getList(PostDao.POST_LANGUAGE);
    }

    public void setLanguage(List<String> languageList) {
        put(PostDao.POST_LANGUAGE, languageList);
    }

}
