package com.infinity.lunadd.util;


import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.controller.EaseUI;
/**
 * Created by DanielChu on 2017/6/9.
 */
public class EaseSettingProvider implements EaseUI.EaseSettingsProvider {
    Boolean msg;
    Boolean song;
    Boolean vibrate;

    public EaseSettingProvider(){
        msg= true;
        song=true;
        vibrate=true;
    }

    public void setMsg(boolean setting){msg=setting;}

    public void setSong(boolean setting){song=setting;}

    public void setVibrate(boolean setting){vibrate=setting;}

    public boolean getMsg(){return msg;}

    public boolean getSong(){return song;}

    public boolean getVibrate(){return vibrate;}

    @Override
    public boolean isMsgNotifyAllowed(EMMessage message) {
        // TODO Auto-generated method stub
        return msg;
    }

    @Override
    public boolean isMsgSoundAllowed(EMMessage message) {
        return song;
    }

    @Override
    public boolean isMsgVibrateAllowed(EMMessage message) {
        return vibrate;
    }

    @Override
    public boolean isSpeakerOpened() {
        return true;
    }


}
