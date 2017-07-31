package com.infinity.lunadd.event;

import android.support.annotation.Nullable;

import com.avos.avoscloud.AVUser;
import com.hyphenate.easeui.domain.User;
/**
 * Created by YX201603-6 on 2016/5/25.
 */
public class AddPeopleEvent {
//    public AVUser mOtherUser;
    public Long mStartTime;
    public int mAction;
    public String muserid;
/*
    public AddPeopleEvent(@Nullable AVUser mOtherUser, @Nullable Long mStartTime, @Nullable int mAction) {
        this.mOtherUser=mOtherUser;
        this.mStartTime=mStartTime;
        this.mAction=mAction;
    }
*/
    public AddPeopleEvent(@Nullable Long mStartTime, @Nullable int mAction, @Nullable String muserid) {
        this.mStartTime=mStartTime;
        this.mAction=mAction;
        this.muserid=muserid;
    }
}
