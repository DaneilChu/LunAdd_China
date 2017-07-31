package com.infinity.lunadd.event;

/**
 * Created by DanielChu on 2017/6/7.
 */
public class HxDisconnectEvent {

    public static final int USER_REMOVED = 0;
    public static final int USER_LOGIN_ANOTHER_DEVICE = 1;
    public static final int CANNOTCONNECTTOHX = 2;
    public static final int NONETWORK = 3;
    private int event;

    public HxDisconnectEvent(int i) {
        this.event = i;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

}
