package com.infinity.lunadd;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.model.EmojiconExampleGroupData;
import com.hyphenate.easeui.receiver.CallReceiver;
import com.hyphenate.util.EMLog;
import com.infinity.lunadd.event.AddPeopleEvent;
import com.infinity.lunadd.event.EventConstant;
import com.infinity.lunadd.mvp.model.rx.RxBus;
import com.infinity.lunadd.mvp.view.home.activity.MainActivity;
import com.infinity.lunadd.util.EaseSettingProvider;
import com.infinity.lunadd.widget.TimeUtil;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;


/**
 * Created by DanielChu on 2017/6/9.
 */
public class EaseUIHelper {
    private EaseUI easeUI;

    private final Context mAppContext;

    private boolean isContactListenerRegisted;

    @Inject
    RxBus mRxBus;


    public EaseUIHelper(Context context) {
        this.mAppContext = context;
    }


    public int getUnreadMessage(){
        return EMClient.getInstance().chatManager().getUnreadMessageCount();

    }

    public void init() {
        LunApplication.getAppComponent().inject(this);
        EMOptions options = initChatOptions();
        if (EaseUI.getInstance().init(mAppContext, options)) {
            easeUI = EaseUI.getInstance();
            easeUI.setSettingsProvider(new EaseSettingProvider());
            setEmojiProvider();
            EMConnectionListener connectionListener = new EMConnectionListener() {
                @Override
                public void onConnected() {

                }

                @Override
                public void onDisconnected(int i) {
                    if (i == EMError.USER_REMOVED) {
                        onCurrentAccountRemoved();
                    } else if (i == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        onConnectionConflict();
                    }

                }
            };
            //注册连接监听
            EMClient.getInstance().addConnectionListener(connectionListener);
            CallReceiver callReceiver = null;
            IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
            if (callReceiver == null) {
                callReceiver = new CallReceiver();
            }

            //注册通话广播接收者
            mAppContext.registerReceiver(callReceiver, callFilter);
            registerEventListener();
            registerGroupAndContactListener();
        }


    }

    private EMOptions initChatOptions(){

        EMOptions options = new EMOptions();
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(false);

        return options;
    }

    private void setEmojiProvider() {
        easeUI.setEmojiconInfoProvider(new EaseUI.EaseEmojiconInfoProvider() {
            @Override
            public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
                EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
                for (EaseEmojicon emojicon : data.getEmojiconList()) {
                    if (emojicon.getIdentityCode().equals(emojiconIdentityCode)) {
                        return emojicon;
                    }
                }
                return null;
            }

            @Override
            public Map<String, Object> getTextEmojiconMapping() {
                return null;
            }
        });

    }

    /**
     * register group and contact listener, you need register when login
     */
    public void registerGroupAndContactListener(){
        if(!isContactListenerRegisted){
            EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
            isContactListenerRegisted = true;
        }
    }

    /**
     * 账号在别的设备登录
     */
    protected void onConnectionConflict() {
        Intent intent = new Intent(mAppContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EaseConstant.ACCOUNT_CONFLICT, true);
        mAppContext.startActivity(intent);
    }

    /**
     * 账号被移除
     */
    protected void onCurrentAccountRemoved() {
        Intent intent = new Intent(mAppContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EaseConstant.ACCOUNT_REMOVED, true);
        mAppContext.startActivity(intent);
    }

    /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void registerEventListener() {
        EMMessageListener messageListener = new EMListener();
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    class EMListener implements EMMessageListener{
        private BroadcastReceiver broadCastReceiver;

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            for (EMMessage message : messages) {
                EMLog.d("EaseuiHelper", "onMessageReceived id : " + message.getMsgId());
                //应用在后台，不需要刷新UI,通知栏提示新消息
                if (!easeUI.hasForegroundActivies()) {
                    easeUI.getNotifier().onNewMsg(message);
                }
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            for (EMMessage message : messages) {
                EMLog.d("EaseuiHelper", "收到透傳消息");
                //获取消息body
                EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                final String action = cmdMsgBody.action();//获取自定义action

                //获取扩展属性 此处省略
                //message.getStringAttribute("");
                EMLog.d("EaseuiHelper", String.format("透傳消息：action:%s,message:%s", action, message.toString()));
                final String str = mAppContext.getString(com.hyphenate.easeui.R.string.receive_the_passthrough);

                final String CMD_TOAST_BROADCAST = "hyphenate.demo.cmd.toast";
                IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);

                if (broadCastReceiver == null) {
                    broadCastReceiver = new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            Toast.makeText(mAppContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
                        }
                    };

                    //注册广播接收者
                    mAppContext.registerReceiver(broadCastReceiver, cmdFilter);
                }

                Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
                broadcastIntent.putExtra("cmd_value", str + action);
                mAppContext.sendBroadcast(broadcastIntent, null);
            }
        }

        @Override
        public void onMessageDelivered(List<EMMessage> messages) {
        }

        @Override
        public void onMessageRead(List<EMMessage> message) {
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {

        }
    }

    /***
     * 好友变化listener
     *
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(String username) {
            Logger.d("onContactAdded");
        }

        @Override
        public void onContactDeleted(String username) {
            Logger.d("onContactDeleted");

        }

        @Override
        public void onContactInvited(String username, String reason) {
            Logger.d("onContactInvited");
            Long dif = (TimeUtil.getCurrentTime().getTime() - Long.parseLong(reason));
            if (dif>=5000){
                try{
                    EMClient.getInstance().contactManager().declineInvitation(username);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                try{
                    EMClient.getInstance().contactManager().acceptInvitation(username);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFriendRequestAccepted(String username) {
            Logger.d("onFriendRequestAccepted");

            mRxBus.post(new AddPeopleEvent(null, EventConstant.ADDFRIENDRESPONDED,username));
        }

        @Override
        public void onFriendRequestDeclined(String username) {
            Logger.d("onFriendRequestDeclined");

        }
    }

    /**
     * get instance of EaseNotifier
     * @return
     */
    public EaseNotifier getNotifier(){
        return easeUI.getNotifier();
    }

    public void pushActivity(Activity activity) {
        easeUI.pushActivity(activity);
    }

    public void popActivity(Activity activity) {
        easeUI.popActivity(activity);
    }
}

