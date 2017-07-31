package com.infinity.lunadd.mvp.model.bean;


import android.support.annotation.NonNull;

import javax.annotation.Resource;

/**
 * Created by DanielChu on 2017/6/8.
 */
public class MessagesBean implements Comparable {
    private String otherusername;
    private String installationId;
    private int action;
    private String content;
    private int contentType;
    private long time;

    public static final int MESSAGE_TYPE_TEXT=0;
    public static final int MESSAGE_TYPE_PHOTO=1;
    public static final int MESSAGE_TYPE_VIDEO_CALL=2;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getcontent() {
        return content;
    }

    public void setcontent(String content) {
        this.content = content;
    }

    public int getContentType() {
        return contentType;
    }

    public void setcontentType(int contentType) {
        this.contentType = contentType;
    }



    public String getOtherUserName() {
        return otherusername;
    }

    public void setOtherUserName(String otherusername) {
        this.otherusername = otherusername;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        //TODO: Should be more comprehensive
        return 0;
    }
}
