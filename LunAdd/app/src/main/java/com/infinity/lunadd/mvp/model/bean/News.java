package com.infinity.lunadd.mvp.model.bean;


import com.avos.avoscloud.AVUser;
import com.hyphenate.easeui.domain.User;

/**
 * Created by DanielChu on 2017/6/8.
 */
public class News {
    private int title;
    private String installationId;
    private int action;
    private String content;
    private String time;
    private AVUser user;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    public AVUser getUser() {
        return user;
    }

    public void setUser(AVUser user) {
        this.user = user;
    }
}
