package com.infinity.lunadd.mvp.model.bean;


import java.util.ArrayList;

/**
 * Created by DanielChu on 2017/6/25.
 */
public class DurationList {
    private ArrayList<Integer> durationList;

    public DurationList(){
        durationList = new ArrayList<>();
        durationList.add(0);
        durationList.add(1);
        durationList.add(3);
        durationList.add(6);
        durationList.add(12);
        durationList.add(24);
        durationList.add(100);
    }

    public ArrayList<Integer> getDurationList() {
        if (durationList!=null) {
            return durationList;
        }else {
            return new ArrayList<>();
        }
    }

    public void setDurationList(ArrayList<Integer> durationList) {
        this.durationList = durationList;
    }
}
