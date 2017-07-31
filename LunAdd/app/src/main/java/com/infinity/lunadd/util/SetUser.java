package com.infinity.lunadd.util;

import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContactManager;
import com.hyphenate.easeui.domain.UserDao;
import com.hyphenate.exceptions.HyphenateException;
import com.infinity.lunadd.mvp.model.rx.RxLeanCloud;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by DanielChu on 2017/6/26.
 */
public class SetUser{

    private RxLeanCloud mRxLeanCloud;
    private PreferenceManager mPreferenceManager;

    @Inject
    public SetUser(RxLeanCloud mRxLeanCloud, PreferenceManager mPreferenceManager){
        this.mRxLeanCloud= mRxLeanCloud;
        this.mPreferenceManager= mPreferenceManager;
    }

    public void deleteChatting(){
        mRxLeanCloud.SaveUserByLeanCloud(defaultUser())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AVUser>() {
                    @Override
                    public void onCompleted() {
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    //move to blacklist
                                    if (mPreferenceManager.getOtherUserID()!=null) {
                                        EMClient.getInstance().contactManager().deleteContact(mPreferenceManager.getOtherUserID());
                                    }
                                } catch (HyphenateException e) {
                                    e.printStackTrace();

                                }
                            }
                        }).start();
                        EMClient.getInstance().chatManager().deleteConversation(mPreferenceManager.getOtherUserID(), true);
                        mPreferenceManager.normalSetting();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(AVUser avUser1) {
                    }
                });
    }


    private AVUser defaultUser(){
        AVUser user = AVUser.getCurrentUser();
        user.put(UserDao.ADDTIME,null);
        user.put(UserDao.WAITING,false);
        user.put(UserDao.DURATION,0);
        user.put(UserDao.OTHERUSERNAME,null);
        user.put(UserDao.OTHERUSERINSTALLATIONID,null);
        user.put(UserDao.OTHERUSERID,null);
        return user;
    }

    //Import the information of other user when receive push
    public boolean setUser(String installationId, final String otheruserid,final String otherusername,final String otheruserInstallationid, final Long startTime){
        AVUser user = AVUser.getCurrentUser();
        if (!installationId.equals(user.get(UserDao.INSTALLATIONID))){
            return false;
        }else{

            user.put(UserDao.OTHERUSERID,otheruserid);
            user.put(UserDao.OTHERUSERINSTALLATIONID,otheruserInstallationid);
            user.put(UserDao.OTHERUSERNAME,otherusername);
            user.put(UserDao.WAITING,false);
            user.put(UserDao.ADDTIME,startTime);

            //TODO: what should I do?
            mPreferenceManager.saveStartTime(startTime);
            mRxLeanCloud.SaveUserByLeanCloud(user)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<AVUser>() {
                        @Override
                        public void onCompleted() {
                            mPreferenceManager.saveOtherUserID(otheruserid);
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        //move to blacklist
                                        EMClient.getInstance().contactManager().removeUserFromBlackList(otheruserid);
                                    } catch (HyphenateException e) {
                                        e.printStackTrace();

                                    }
                                }
                            }).start();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(AVUser avUser1) {
                        }
                    });

            return true;

        }
    }
}
