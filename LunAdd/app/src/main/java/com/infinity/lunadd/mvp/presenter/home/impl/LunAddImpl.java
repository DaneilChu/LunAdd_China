package com.infinity.lunadd.mvp.presenter.home.impl;


import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.domain.UserDao;
import com.hyphenate.exceptions.HyphenateException;
import com.infinity.lunadd.base.BaseView;
import com.infinity.lunadd.event.AddPeopleEvent;
import com.infinity.lunadd.event.EventConstant;
import com.infinity.lunadd.mvp.model.bean.MessagesBean;
import com.infinity.lunadd.mvp.model.bean.News;
import com.infinity.lunadd.mvp.model.rx.RxBus;
import com.infinity.lunadd.mvp.model.rx.RxLeanCloud;
import com.infinity.lunadd.mvp.presenter.home.ILunAddPresenter;
import com.infinity.lunadd.mvp.presenter.home.IProfilePresenter;
import com.infinity.lunadd.mvp.view.home.ILunAddView;
import com.infinity.lunadd.mvp.view.home.IProfileView;
import com.infinity.lunadd.util.PreferenceManager;
import com.infinity.lunadd.util.SetUser;
import com.infinity.lunadd.widget.TimeUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by DanielChu on 2017/6/9.
 */
public class LunAddImpl implements ILunAddPresenter {

    private final PreferenceManager mPreferenceManager;
    private final SetUser mSetUser;
    private ILunAddView mLunAddView;
    private final RxLeanCloud mRxLeanCloud;
    private final RxBus mRxBus;
    private List< MessagesBean> messageBeanList = new ArrayList<>();
    private int duration;
    private Handler handler;
    private boolean addFriendResponded;
    private String addFriendResponded_otheruserid;
    public AVUser user;
    private Subscription mSubscription;
    public static final String TAG = LunAddImpl.class.getSimpleName();



    @Inject
    public LunAddImpl(RxLeanCloud rxLeanCloud, RxBus rxBus, PreferenceManager preferenceManager, SetUser setUser) {
        handler = new Handler();
        mPreferenceManager = preferenceManager;
        mRxBus = rxBus;
        mRxLeanCloud = rxLeanCloud;
        mSetUser=setUser;
        user = AVUser.getCurrentUser();
        Logger.init(TAG);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void initData() {
        mLunAddView.showLoading();
        initialView();
        addFriendResponded = false;
        addFriendResponded_otheruserid = null;
        mSubscription=registerEvent();
        mLunAddView.hideProgress();
    }

    private void initialView(){
        if (user.get(UserDao.OTHERUSERID)!=null){
            initialDuration();
            initTime();
            toMode(1);
            loadMessage();
        } else if (!user.getBoolean(UserDao.WAITING)){
            toMode(2);
        }else{
            toMode(3);
        }
    }

    private Subscription registerEvent() {
        return mRxBus.toObservable(AddPeopleEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<AddPeopleEvent>() {
                    @Override
                    public void call(AddPeopleEvent event) {
                        Logger.d("receive something");
                        Logger.d(Integer.toString(event.mAction));
                        switch (event.mAction) {
                            case EventConstant.ADDPEOPLE:
                                //TODO:
                                initialView();
                                break;
                            case EventConstant.ADDFRIENDRESPONDED:
                                Logger.d("EventConstant.ADDFRIENDRESPONDED");
                                addFriendResponded = true;
                                addFriendResponded_otheruserid = event.muserid;
                                break;
                            default:
                                break;
                        }
                    }
                });
    }



    @Override
    public void initTime(){
        if (mPreferenceManager.getStartTime()!=0){
            //設定定時要執行的方法
            removeHandler();
            //設定Delay的時間
            handler.postDelayed(updateTimer, 1000);
        }
    }

    //固定要執行的方法
    private Runnable updateTimer = new Runnable() {
        public void run() {
            long rt = remainingTime();
            Logger.d(Long.toString(rt));
            if (rt<=0){
                Logger.d("remainingTime<0");
                mLunAddView.refresh();
                handler.removeCallbacks(this);
            }else {
                mLunAddView.updateTime(rt);
                //Repeat every 60 seconds in order to save battery
                handler.postDelayed(this, 60000);
            }
        }
    };

    private void initialDuration(){
        duration = user.getInt(UserDao.DURATION);
    }

    //Only delete the message and hiding the chating container, delete id etc are not included
    //Not Used and the implementation move to SetUser
//    @Override
//    public void removeAllMessage(User user){
//        EMClient.getInstance().chatManager().deleteConversation(user.getOtheruserid(), true);
//    }

    private User clearOtherUserInformation(User user){
        user.setOtherusername(null);
        user.setOtheruserInstallationId(null);
        user.setOtheruserid(null);
        return user;
    }
    //Mode 2: Only show button (not chatting with other user)
    //Mode 1: Show chat container, time, etc
    //Mode 3: Show waiting
    private void toMode(int mode){
        switch (mode) {
            case 1:
                mLunAddView.showAddButton(false);
                mLunAddView.showChatContainer(true);
                mLunAddView.showRemainingTime(true, remainingTime());
                mLunAddView.showWaiting(false);
                mLunAddView.showSelector(false);
                break;
            case 2:
                mLunAddView.showAddButton(true);
                mLunAddView.showChatContainer(false);
                mLunAddView.showRemainingTime(false, 0);
                mLunAddView.showWaiting(false);
                mLunAddView.showSelector(true);
                break;
            case 3:
                mLunAddView.showAddButton(false);
                mLunAddView.showChatContainer(false);
                mLunAddView.showRemainingTime(false, 0);
                mLunAddView.showWaiting(true);
                mLunAddView.showSelector(false);
                break;

        }
    }

    @Override
    public void removePeople(){
        Logger.d("Remove People");
        mSetUser.deleteChatting();
        duration = 0;
        messageBeanList.clear();
        removeHandler();
        toMode(2);
    }

    @Override
    public void removeHandler(){
        if (handler!=null) {
            handler.removeCallbacks(updateTimer);
        }
    }

    private AVUser addPeople(List<AVUser> otheruserList){
        AVUser finaluser = null;
        for (AVUser otheruser : otheruserList) {
            try {
                //demo use a hardcode reason here, you need let user to input if you like
                EMClient.getInstance().contactManager().addContact(otheruser.getUsername(), Long.toString(TimeUtil.getCurrentTime().getTime()));
                Logger.d("addPeople");
                Logger.d(Thread.currentThread().getName());
                Thread.sleep(5000);
                if (addFriendResponded){
                    Logger.d("addFriendResponded");
                    for (AVUser otheruser2 : otheruserList) {
                        if (otheruser2.getUsername().equals(addFriendResponded_otheruserid)){
                            finaluser = otheruser2;
                        }
                    }
                    addFriendResponded=false;
                    break;
                }
            } catch (final Exception e) {
                e.printStackTrace();

            }
        }
        return finaluser;
    }

    @Override
    //TODO: If two users are commiting at the same time, they will probably get the same user and the user can get the message from both the users.
    public void commit(final int duration, final String content){
        toMode(3);
        this.duration=duration;
        Logger.e("StartCommit");
        mRxLeanCloud.GetUserListByUserStatus(true,mPreferenceManager.getPreferlanguageLanguage(),duration, TimeUtil.getLastDayTime())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<List<AVUser>, Observable<AVUser>>() {
                    @Override
                    public Observable<AVUser> call(List<AVUser> otheruserList) {
                        if (otheruserList.size()!=0){
                            Logger.d(Thread.currentThread().getName());
                            AVUser otheruser = addPeople(otheruserList);
                            if (otheruser!=null) {
                                mPreferenceManager.saveOtherUserID(otheruser.getUsername());
                                user.put(UserDao.OTHERUSERID, otheruser.getUsername());
                                user.put(UserDao.OTHERUSERNAME, otheruser.getString(UserDao.NICK));
                                user.put(UserDao.OTHERUSERINSTALLATIONID, otheruser.getString(UserDao.INSTALLATIONID));
                                user.put(UserDao.WAITING, false);
                                user.put(UserDao.ADDTIME, System.currentTimeMillis());
                                user.put(UserDao.DURATION, duration);
                            }else{
                                Logger.d("otheruser is null");
                                user.put(UserDao.WAITING,true);
                                user.put(UserDao.DURATION,duration);
                            }
                        }else {
                            user.put(UserDao.WAITING,true);
                            user.put(UserDao.DURATION,duration);
                        }
                        return mRxLeanCloud.SaveUserByLeanCloud(user);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<AVUser, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(AVUser otheruser) {
                        Logger.e("Reach PushToOtherUser");
                        return mRxLeanCloud.PushToOtherUser(user,content,0);
                    }
                })
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        //Find other user successfully
                        if (mPreferenceManager.getOtherUserID()!=null){
                            mPreferenceManager.saveStartTime(System.currentTimeMillis());
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        //move to blacklist
                                        EMClient.getInstance().contactManager().removeUserFromBlackList(mPreferenceManager.getOtherUserID());
                                    } catch (HyphenateException e) {
                                        e.printStackTrace();

                                    }
                                }
                            }).start();
                            toMode(1);
                            loadMessage();
                            //TODO: build timer and push to other user
                            mLunAddView.enableAlarmService(duration);
                            mLunAddView.hideProgress();
                        }else{
                            //fail to find user and wait for response

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        mLunAddView.showError();
                    }

                    @Override
                    public void onNext(Boolean succ) {
                        if (succ){
                            Logger.d("Push Successful");
                        }
                    }
                });
    }
/*
    @Override
    //TODO: If two users are commiting at the same time, they will probably get the same user and the user can get the message from both the users.
    public void commit(final int duration, final String content){
        toMode(3);
        this.duration=duration;
        Logger.e("StartCommit");
        mRxLeanCloud.GetUserByUserStatus(true,mPreferenceManager.getPreferlanguageLanguage(),duration)
                .flatMap(new Func1<AVUser, Observable<AVUser>>() {
                    @Override
                    public Observable<AVUser> call(AVUser otheruser) {
                        if (otheruser!=null){
                            mPreferenceManager.saveOtherUserID(otheruser.getUsername());
                            user.put(UserDao.OTHERUSERID,otheruser.getUsername());
                            user.put(UserDao.OTHERUSERNAME,otheruser.getString(UserDao.NICK));
                            user.put(UserDao.OTHERUSERINSTALLATIONID,otheruser.getString(UserDao.INSTALLATIONID));
                            user.put(UserDao.WAITING,false);
                            user.put(UserDao.ADDTIME,System.currentTimeMillis());
                            user.put(UserDao.DURATION,duration);
                        }else {
                            user.put(UserDao.WAITING,true);
                            user.put(UserDao.DURATION,duration);
                        }
                        return mRxLeanCloud.SaveUserByLeanCloud(user);
                        }
                    })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<AVUser, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(AVUser otheruser) {
                        Logger.e("Reach PushToOtherUser");
                        return mRxLeanCloud.PushToOtherUser(user,content,0);
                    }
                })
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        //Find other user successfully
                        if (mPreferenceManager.getOtherUserID()!=null){
                            mPreferenceManager.saveStartTime(System.currentTimeMillis());
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        //move to blacklist
                                        EMClient.getInstance().contactManager().removeUserFromBlackList(mPreferenceManager.getOtherUserID());
                                    } catch (HyphenateException e) {
                                        e.printStackTrace();

                                    }
                                }
                            }).start();
                            toMode(1);
                            loadMessage();
                            //TODO: build timer and push to other user
                            mLunAddView.enableAlarmService(duration);
                            mLunAddView.hideProgress();
                        }else{
                            //fail to find user and wait for response

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        mLunAddView.showError();
                    }

                    @Override
                    public void onNext(Boolean succ) {
                        if (succ){
                            Logger.d("Push Successful");
                        }
                    }
                });
    }
*/

    private long remainingTime(){
        Long spentTime = System.currentTimeMillis() - mPreferenceManager.getStartTime();
        //calculate the remaining time
        Logger.d(Long.toString(System.currentTimeMillis()));
        Logger.d(Long.toString(mPreferenceManager.getStartTime()));
        Logger.d(Long.toString(spentTime));
        Logger.d(Long.toString(duration));
        if (duration!=0) {
            return duration - (spentTime / 1000) / 60 / 24;
        }else{
            return 0;
        }
    }

    @Override
    public void loadMessage(){
        messageBeanList.clear();
        List< EMConversation> emConversationList = loadConversationList();
        int conversationSize = emConversationList.size();

        Set<MessagesBean> messagesBeanSet = new TreeSet<>();

        if (conversationSize!=0) {
            Logger.d("conversationSize!=0");
            for (int i = 0; i < conversationSize; i++) {
                EMConversation emConversation = emConversationList.get(i);

                EMMessage emMessage = emConversation.getLastMessage();
                if (emMessage.getUserName().equals(user.getString(UserDao.OTHERUSERID))) {
                    MessagesBean messagesBean = new MessagesBean();

                    messagesBean.setOtherUserName(user.getString(UserDao.OTHERUSERNAME));
                    if (emMessage.getBody() instanceof EMTextMessageBody) {
                        messagesBean.setcontentType(MessagesBean.MESSAGE_TYPE_TEXT);
                        messagesBean.setcontent(((EMTextMessageBody) emMessage.getBody()).getMessage());
                    } else if (emMessage.getBody() instanceof EMImageMessageBody) {
                        messagesBean.setcontentType(MessagesBean.MESSAGE_TYPE_PHOTO);
                    }
                    messagesBean.setTime(emMessage.getMsgTime());

                    messagesBeanSet.add(messagesBean);
                }
            }
            messageBeanList.addAll(new ArrayList<>(messagesBeanSet));
            mLunAddView.setChatting(messageBeanList);
        }else{
            Logger.d("conversationSize=0");

            mLunAddView.setEmptyChatting(user.getString(UserDao.OTHERUSERNAME));
        }

    }
/*
    @Override
    public void onMessageReceived(List< EMMessage> list) {

        Set<MessagesBean> messagesBeanSet = new TreeSet<>(messageBeanList);

        for (EMMessage emMessage : list) {
            MessagesBean messagesBean = new MessagesBean();
            messagesBean.setOtherUserName(user.getString(UserDao.OTHERUSERNAME));
            if (emMessage.getBody() instanceof EMTextMessageBody) {
                messagesBean.setcontentType(MessagesBean.MESSAGE_TYPE_TEXT);
                messagesBean.setcontent(((EMTextMessageBody) emMessage.getBody()).getMessage());
            } else if (emMessage.getBody() instanceof EMImageMessageBody) {
                messagesBean.setcontentType(MessagesBean.MESSAGE_TYPE_PHOTO);
            }
            messagesBean.setTime(emMessage.getMsgTime());
            messagesBeanSet.add(messagesBean);
        }
        messageBeanList.clear();
        messageBeanList.addAll(new ArrayList<>(messagesBeanSet));
        mLunAddView.setChatting(messageBeanList);
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        // 收到透傳消息
    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {
        // 收到已讀回執
    }

    @Override
    public void onMessageRead(List<EMMessage> message) {
        // 收到已送達回執
    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {
        // 消息狀態變動
    }
*/
    /**
     * 获取所有会话
     *
     * @param
     * @return +
     */
    private List< EMConversation> loadConversationList() {
        // get all conversations
        Map< String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair< Long, EMConversation>> sortList = new ArrayList< Pair< Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List< EMConversation> list = new ArrayList< EMConversation>();
        for (Pair< Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    private void sortConversationByLastChatTime(List< Pair< Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator< Pair< Long, EMConversation>>() {
            @Override
            public int compare(final Pair< Long, EMConversation> con1, final Pair< Long, EMConversation> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    @Override
    public void attachView(@NonNull BaseView view) {
        mLunAddView = (ILunAddView) view;
    }

    @Override
    public void detachView() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            Logger.d("mSubscription.unsubscribe");
            mSubscription.unsubscribe();
        }
        mLunAddView = null;
        removeHandler();
    }
}
