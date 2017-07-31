package com.infinity.lunadd.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.infinity.lunadd.di.scope.ContextLife;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by DanielChu on 2017/6/9.
 */
public class PreferenceManager {

    private static final String SETTING = "lunaddsetting";
    private static final String USERID = "userid";
    private static final String USERPASSWORD = "userpassword";
    private static final String USERNAME = "username";
    private static final String FIRST_TIME = "firsttime";
    private static final String IS_LOGIN = "islogin";
    private static final boolean FIRST_TIME_DEFAULT = true;
    private static final String OTHER_USER_ID = "otheruserid";
    private static final String NOTIFY = "notify";
    private static final String ISSONG = "issong";
    //private static final String SONG = "song";
    private static final String ISSHAKE = "isshake";
    private static final String LANGUAGE = "language";
    private static final String PREFERLANGUAGE = "preferlanguage";
    private static final String STARTTIME = "starttime";


    private static SharedPreferences mSharedPreferences;


    //Not Include userID, userPassword, etc
    public void normalSetting(){
        saveOtherUserID(null);
        saveStartTime(0);
    }

    public PreferenceManager(@ContextLife() Context context) {
        mSharedPreferences = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
    }


    public void saveCurrentUserId(String id) {
        mSharedPreferences.edit().putString(USERID, id).apply();
    }

    public String getCurrentUserId() {
        return mSharedPreferences.getString(USERID, null);

    }

    public void saveCurrentUserPassword(String pass) {
        mSharedPreferences.edit().putString(USERPASSWORD, pass).apply();
    }

    public String getCurrentUserPassword() {
        return mSharedPreferences.getString(USERPASSWORD, null);

    }

    public void saveStartTime(long time) {
        mSharedPreferences.edit().putLong(STARTTIME, time).apply();
    }

    public Long getStartTime() {
        return mSharedPreferences.getLong(STARTTIME, 0);
    }

    public void saveCurrentUserName(String name) {
        mSharedPreferences.edit().putString(USERNAME, name).apply();
    }

    public String getCurrentUserName() {
        return mSharedPreferences.getString(USERNAME, null);

    }

    public void setNotify(boolean notify) {
        mSharedPreferences.edit().putBoolean(NOTIFY, notify).apply();
    }

    public boolean getNotify() {
        return mSharedPreferences.getBoolean(NOTIFY, true);

    }

    public void setIssong(boolean issong) {
        mSharedPreferences.edit().putBoolean(ISSONG, issong).apply();
    }

    public boolean getIssong() {
        return mSharedPreferences.getBoolean(ISSONG, true);

    }

    /*public void setSong(int song) {
        mSharedPreferences.edit().putInt(SONG, song).apply();
    }

    public int getSong() {
        return mSharedPreferences.getInt(SONG, 0);
    }
*/
    public void setIsshake(boolean isshake) {
        mSharedPreferences.edit().putBoolean(ISSHAKE, isshake).apply();
    }

    public boolean getIsshake() {
        return mSharedPreferences.getBoolean(ISSHAKE, true);

    }

    public void setLanguage(int language) {
        mSharedPreferences.edit().putInt(LANGUAGE, language).apply();
    }

    public int getLanguage() {
        return mSharedPreferences.getInt(LANGUAGE, 0);

    }

//    public void setPreferlanguageLanguage(List<String> preferlanguage) {
//        ArrayList<String> pre = new ArrayList<String>(preferlanguage);
//        tinydb.putListString(PREFERLANGUAGE, pre);
//    }

    public void setPreferlanguageLanguage(List<String> preferlanguage) {
        Set<String> pre = new HashSet<String>();
        pre.addAll(preferlanguage);
        mSharedPreferences.edit().putStringSet(PREFERLANGUAGE, pre).apply();
    }

    public List<String> getPreferlanguageLanguage() {
        Set<String> pre = mSharedPreferences.getStringSet(PREFERLANGUAGE, null);
        if (pre!=null) {
            List<String> preferlanguage = new ArrayList<String>(pre);
            return preferlanguage;
        }else {
            return null;
        }
    }

    public boolean isLogined() {
        return mSharedPreferences.getBoolean(IS_LOGIN, false);
    }

    public void setIslogin(boolean islogin) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(IS_LOGIN, islogin);
        editor.apply();

    }

    public boolean isFirstTime() {
        return mSharedPreferences.getBoolean(FIRST_TIME, FIRST_TIME_DEFAULT);
    }

    public void saveFirsttime(boolean isFirst) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(FIRST_TIME, isFirst);
        editor.apply();

    }


    public String getOtherUserID() {
        return mSharedPreferences.getString(OTHER_USER_ID, null);
    }

    public void saveOtherUserID(String ID) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(OTHER_USER_ID, ID);
        editor.apply();
    }


    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

}
