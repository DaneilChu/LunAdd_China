package com.hyphenate.easeui.domain;

/**
 * Created by DanielChu on 2017/6/5.
 * All the parameter except USERID can be gotten and modified in User class
 */
public class UserDao {
    public static final String NICK = "nick";
    public static final String OTHERUSERID = "otheruserid";
    public static final String PREFERLANGUAGE = "preferlanguage";
    public static final String OTHERUSERNAME = "otherusername";
    public static final String USERID = "username"; //set by setUsername in AVUser Class
    public static final String WAITING = "waiting";
    public static final String BACKGROUND = "background";
    public static final String INSTALLATIONID = "userinstallationId";
    public static final String OTHERUSERINSTALLATIONID = "otheruserinstallationId";
    public static final String DURATION = "duration";
    public static final String ADDTIME = "addtime";
    public static final String UPDATEDAT = "updatedAt";
}
