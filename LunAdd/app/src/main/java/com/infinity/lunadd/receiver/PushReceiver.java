package com.infinity.lunadd.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.hyphenate.easeui.domain.User;
import com.infinity.lunadd.LunApplication;
import com.infinity.lunadd.event.AddPeopleEvent;
import com.infinity.lunadd.event.EventConstant;
import com.infinity.lunadd.mvp.model.dao.NewsDao;
import com.infinity.lunadd.mvp.model.rx.RxBus;
import com.infinity.lunadd.mvp.view.home.activity.MainActivity;
import com.infinity.lunadd.util.SetUser;
import com.infinity.lunadd.R;
import com.orhanobut.logger.Logger;


import org.json.JSONObject;

import javax.inject.Inject;


/**
 * Created by DanielChu on 2017/6/26.
 */
public class PushReceiver extends BroadcastReceiver {

    @Inject
    SetUser setUser;
    @Inject
    RxBus mRxBus;



    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("Receive Push");
        LunApplication.getAppComponent().inject(this);
        try {
            if (intent.getAction().equals("com.infinity.lunadd.Push")) {
                Logger.d("Receive Push 2");
                JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));

                final String message = json.getString(NewsDao.CONTENT);
                final String installationId = json.getString(NewsDao.INSTALLATIONI_ID);
                final Long start_time = json.getLong(NewsDao.TIME);
                final String title = context.getString(json.getInt(NewsDao.TITLE));
                final String otherusername = json.getString(NewsDao.USERNAME);
                final String otheruserid = json.getString(NewsDao.USERID);
                final String otheruserInstallationid = json.getString(NewsDao.USERINSTALLATIONID);



                if (setUser.setUser(installationId,otheruserid,otherusername,otheruserInstallationid,start_time)){
                    Intent resultIntent = new Intent(AVOSCloud.applicationContext, MainActivity.class);
                    PendingIntent pendingIntent =
                            PendingIntent.getActivity(AVOSCloud.applicationContext, 0, resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(AVOSCloud.applicationContext)
                                    .setSmallIcon(R.drawable.logo)
                                    .setContentTitle(title)
                                    .setContentText(message)
                                    .setTicker(message);
                    mBuilder.setContentIntent(pendingIntent);
                    mBuilder.setAutoCancel(true);

                    int mNotificationId = 28;
                    NotificationManager mNotifyMgr =
                            (NotificationManager) AVOSCloud.applicationContext
                                    .getSystemService(
                                            Context.NOTIFICATION_SERVICE);

                    mRxBus.post(new AddPeopleEvent(start_time, EventConstant.ADDPEOPLE,null));

                    mNotifyMgr.notify(mNotificationId, mBuilder.build());
                }

            }else{
                Logger.d(intent.getAction());
                Logger.d("Fail to receive");
            }
        } catch (Exception e) {

        }
    }
}