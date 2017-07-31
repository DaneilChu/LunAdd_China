package com.infinity.lunadd.mvp.view.home;


import com.infinity.lunadd.base.BaseView;
import com.infinity.lunadd.mvp.model.bean.MessagesBean;

import java.util.List;

/**
 * Created by DanielChu on 2017/6/8.
 */
public interface ILunAddView extends BaseView {

    void addPeople();

    void showAddButton(boolean show);

    void showWaiting(boolean show);

  //  void showSuccess(boolean show);

    void showChatContainer(boolean show);

    void showRemainingTime(boolean show, long hour);

    void showSelector(boolean show);

    void toChatActivity();

    void updateTime(long hour);

    void setEmptyChatting(String otherUserName);

    void setChatting(List<MessagesBean> messageBeanList);

    void refresh();

    void refreshMessage();

    boolean deleteChat();

    void enableAlarmService(int duration);

    void cancelAlarmService();




}
