package com.infinity.lunadd.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.infinity.lunadd.LunApplication;
import com.infinity.lunadd.util.SetUser;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;


/**
 * Created by DanielChu on 2017/6/26.
 */
public class DurationTimerReceiver extends BroadcastReceiver {
    public static final String NOTIFICATION_ID = "notification_id";
    public static final int NOTIFICATION_1 = 27;
    public static final String MSG = "msg";
    public static final String CHAT_END = "chat_end";
    public static final String NOTIFICATION = "notification";

    @Inject
    SetUser setUser;


    @Override
    public void onReceive(Context context, Intent intent) {
        LunApplication.getAppComponent().inject(this);
        //Do something every 30 seconds
        //TODO:
        Logger.d("receive something");
        try {
            Bundle bData = intent.getExtras();
            if(bData.get(MSG).equals(CHAT_END)){
                //TODO: Need to complete the task of BOOT_COMPLETED
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = intent.getParcelableExtra(NOTIFICATION);
                int id = intent.getIntExtra(NOTIFICATION_ID, 0);
                notificationManager.notify(id, notification);
                setUser.deleteChatting();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e.getMessage());
        }
    }
}