package com.hyphenate.easeui.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.avos.avoscloud.AVUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DanielChu on 2017/6/5.
 */
public class User extends AVUser implements Parcelable {
    private String nick;
    private ArrayList<String> preferLanguage;
    private boolean waiting;
    private String otherusername;

    public User() {

    }

    protected User(Parcel in) {
        this.nick = in.readString();
        this.preferLanguage = in.readArrayList(String.class.getClassLoader());
        this.waiting = in.readByte()!=0;
        this.otherusername = in.readString();
    }

    public String getOtherusername() {
        return getString(UserDao.OTHERUSERNAME);
    }

    public void setOtherusername(String otherusername) {
        put(UserDao.OTHERUSERNAME, otherusername);
    }

    public String getOtheruserInstallationId() {
        return getString(UserDao.OTHERUSERINSTALLATIONID);
    }

    public void setDuration(int duration) {
        put(UserDao.DURATION, duration);
    }

    public int getDuration() {
        return getInt(UserDao.DURATION);
    }

    public void setOtheruserInstallationId(String otheruserinstallationid) {
        put(UserDao.OTHERUSERINSTALLATIONID, otheruserinstallationid);
    }

    public String getInstallationId() {
        return getString(UserDao.INSTALLATIONID);
    }

    public void setInstallationId(String installationid) {
        put(UserDao.INSTALLATIONID, installationid);
    }

    public String getOtheruserid() {
        return getString(UserDao.OTHERUSERID);
    }

    public void setOtheruserid(String otheruserid) {
        put(UserDao.OTHERUSERID, otheruserid);
    }

    public String getBackground() {
        return getString(UserDao.BACKGROUND);
    }

    public void setBackground(String background) {
        put(UserDao.BACKGROUND, background);
    }


    public String getNick() {
        return getString(UserDao.NICK);
    }

    public void setNick(String nick) {
        put(UserDao.NICK, nick);
    }

    public List<String> getPreferLanguage() {
        return (List<String>) get(UserDao.PREFERLANGUAGE);
    }

    public void setPreferLanguage(List<String> preferLanguage) {
        put(UserDao.PREFERLANGUAGE, preferLanguage);
    }


    public boolean getWaiting() {
        return getBoolean(UserDao.WAITING);
    }

    public void setWaiting(boolean waiting) {
        put(UserDao.WAITING, waiting);
    }

    public Long getAddTime() {
        return getLong(UserDao.ADDTIME);
    }

    public void setAddTime(Long time) {
        put(UserDao.ADDTIME, time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nick);
        dest.writeList(this.preferLanguage);
        dest.writeByte((byte) (this.waiting?1:0));
        dest.writeString(this.otherusername);
    }

    public void initialize(){
        setAddTime(null);
        setDuration(0);
        setOtheruserInstallationId(null);
        setWaiting(false);
        setOtherusername(null);
    }




}
