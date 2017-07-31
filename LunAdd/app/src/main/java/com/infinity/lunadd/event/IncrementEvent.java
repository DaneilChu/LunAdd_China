package com.infinity.lunadd.event;

/**
 * Created by DanielChu on 2017/6/7.
 */
public class IncrementEvent {
    public int position;
    public int count;

    public IncrementEvent(int count, int position) {
        this.count = count;
        this.position = position;
    }


}
