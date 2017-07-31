package com.infinity.lunadd.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

/**
 * Created by DanielChu on 2017/6/8.
 */
public class LunReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("receive something");
        try {
            if (intent.getAction().equals("com.infinity.lunadd.Push")) {
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e.getMessage());
        }

    }

}
