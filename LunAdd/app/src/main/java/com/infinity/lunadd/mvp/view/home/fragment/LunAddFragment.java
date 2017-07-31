package com.infinity.lunadd.mvp.view.home.fragment;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.domain.UserDao;
import com.hyphenate.easeui.ui.ChatActivity;
import com.hyphenate.util.DateUtils;
import com.hyphenate.util.NetUtils;
import com.infinity.lunadd.R;
import com.infinity.lunadd.adapter.DurationAdapter;
import com.infinity.lunadd.base.BaseFragment;
import com.infinity.lunadd.mvp.model.bean.DurationList;
import com.infinity.lunadd.mvp.model.bean.MessagesBean;
import com.infinity.lunadd.mvp.presenter.home.impl.LunAddImpl;
import com.infinity.lunadd.mvp.view.home.ILunAddView;
import com.infinity.lunadd.mvp.view.home.activity.MainActivity;
import com.infinity.lunadd.mvp.view.login.activity.LoginActivity;
import com.infinity.lunadd.receiver.DurationTimerReceiver;
import com.orhanobut.logger.Logger;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by DanielChu on 2017/6/16.
 */
public class LunAddFragment extends BaseFragment implements ILunAddView {
    @Inject
    LunAddImpl mLunAddImpl;
    @BindView(R.id.chat_container)
    RelativeLayout chat_container;
    @BindView(R.id.tv_otherusername)
    TextView tv_otherusername;
    @BindView(R.id.tv_chatcontent)
    TextView tv_chatcontent;
    @BindView(R.id.tv_chattime)
    TextView tv_chattime;
    @BindView(R.id.duration_selector)
    RecyclerView durationSelector;
    @BindView(R.id.duration_information)
    TextView durationInfromation;
    @BindView(R.id.btn_waiting)
    Button btn_waiting;
    @BindView(R.id.btn_lunadd)
    Button btn_lunadd;
    @BindView(R.id.tv_remaining_time)
    TextView tv_remaining_time;
    @BindView(R.id.fl_error_item)
    FrameLayout errorItemContainer;


    protected boolean isConflict;

    private final static int MSG_REFRESH = 2;
    public float firstItemWidth;
    public float padding;
    public float itemWidth;
    public int allPixelsDate;
    public int finalWidth;
    private DurationAdapter mDurationAdapter;
    private DurationList durationList;
    private ViewTreeObserver vtoDate;
    private TextView errorText;


    @Override
    public void initData() {
        mLunAddImpl.initData();
    }

    @Override
    public void initViews() {
//        View errorView = (LinearLayout) View.inflate(getActivity(),R.layout.em_chat_neterror_item, null);
//        errorItemContainer.addView(errorView);
//        errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);
        EMClient.getInstance().addConnectionListener(connectionListener);
        //  getRecyclerViewDuration();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_lunadd;
    }

    @Override
    public void initDagger() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mLunAddImpl.detachView();
        EMClient.getInstance().removeConnectionListener(connectionListener);
    }

    @Override
    public void onPause(){
        super.onPause();
        mLunAddImpl.removeHandler();
        Logger.d("LunAddFragment on Pause");
    }

    @Override
    public void initPresenter() {
        mLunAddImpl.attachView(this);
    }

    @OnClick(R.id.chat_container)
    @Override
    public void toChatActivity(){
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(EaseConstant.EXTRA_USER_ID, AVUser.getCurrentUser().getString(UserDao.OTHERUSERID));
        intent.putExtra(EaseConstant.EXTRA_USER_NICK, AVUser.getCurrentUser().getString(UserDao.OTHERUSERNAME));
        startActivity(intent);

    }

    @OnLongClick(R.id.chat_container)
    @Override
    public boolean deleteChat(){
        showWarningDialog(getString(R.string.delete), getString(R.string.delete_chat), new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                refresh();
            }
        });
        return true;
    }

    @OnClick(R.id.btn_waiting)
    public void deleteWait(){
        showWarningDialog(getString(R.string.delete), getString(R.string.delete_chat), new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                refresh();
            }
        });
    }

    public static LunAddFragment newInstance() {
        return new LunAddFragment();
    }

    @Override
    public void onResume(){
        super.onResume();
        mLunAddImpl.initTime();
        mLunAddImpl.loadMessage();
    }

    @OnClick(R.id.btn_lunadd)
    @Override
    public void addPeople(){
        mLunAddImpl.commit(durationList.getDurationList().get(mDurationAdapter.getSelecteditem()),getString(R.string.addpeople_push_content));
    }

    @Override
    public void showAddButton(boolean show){
        if (btn_lunadd!=null) {
            if (show) {
                btn_lunadd.setVisibility(View.VISIBLE);
            } else {
                btn_lunadd.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showWaiting(boolean show){
        if (btn_waiting!=null) {

            if (show) {
                btn_waiting.setVisibility(View.VISIBLE);
            } else {
                btn_waiting.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showChatContainer(boolean show){
        if (chat_container!=null) {

            if (show) {
                chat_container.setVisibility(View.VISIBLE);
            } else {
                chat_container.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showRemainingTime(boolean show, long hr){
        if (tv_remaining_time!=null) {

            if (show) {
                tv_remaining_time.setVisibility(View.VISIBLE);
                updateTime(hr);
            } else {
                tv_remaining_time.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showSelector(boolean show){
        if (durationSelector!=null && durationInfromation!=null) {

            if (show) {
                durationSelector.setVisibility(View.VISIBLE);
                durationInfromation.setVisibility(View.VISIBLE);
                //Prevent the recycle view is initialized twice
                if (vtoDate == null) {
                    getRecyclerViewDuration();
                }
            } else {
                durationSelector.setVisibility(View.GONE);
                durationInfromation.setVisibility(View.GONE);

            }
        }
    }

    @Override
    public void setChatting(final List<MessagesBean> messageBeanList){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chat_container.getVisibility() == View.VISIBLE) {
                    for (MessagesBean messageBean : messageBeanList) {
                        tv_otherusername.setText(messageBean.getOtherUserName());
                        switch (messageBean.getContentType()) {
                            case MessagesBean.MESSAGE_TYPE_PHOTO:
                                tv_chatcontent.setText(getString(R.string.photo));
                                break;
                            case MessagesBean.MESSAGE_TYPE_TEXT:
                                tv_chatcontent.setText(messageBean.getcontent());
                                break;
                            case MessagesBean.MESSAGE_TYPE_VIDEO_CALL:
                                tv_chatcontent.setText(getString(R.string.video_call));
                                break;
                        }
                        tv_chattime.setText((DateUtils.getTimestampString(new Date(
                                messageBean.getTime()))));
                    }
                }
            }
        });
    }

    @Override
    public void setEmptyChatting(String otherUsername){
        tv_otherusername.setText(otherUsername);
        tv_chatcontent.setText(null);
        tv_chattime.setText(null);

    }


    @Override
    public void updateTime(long hr){
        String time = String.format(getString(R.string.remaining_time_default),hr);
        tv_remaining_time.setText(time);
    }

    //Delete the conversation, alarm service, etc
    @Override
    public void refresh(){
        Logger.d("Refresh");
        showLoading();
        //TODO:
        cancelAlarmService();
        mLunAddImpl.removePeople();
        if (getActivity()instanceof MainActivity){
            //TODO: What should I do? Leave for update
            ((MainActivity) getActivity()).clearUnreadLabel();
        }
        hideProgress();
    }

    protected EMConnectionListener connectionListener = new EMConnectionListener() {

        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED || error == EMError.USER_LOGIN_ANOTHER_DEVICE || error == EMError.SERVER_SERVICE_RESTRICTED) {
                isConflict = true;
            } else {
                handler.sendEmptyMessage(0);
            }
        }

        @Override
        public void onConnected() {
            handler.sendEmptyMessage(1);
        }
    };

    @Override
    public void refreshMessage(){
        if(!handler.hasMessages(MSG_REFRESH)){
            handler.sendEmptyMessage(MSG_REFRESH);
        }
    }



    protected Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    onConnectionDisconnected();
                    break;
                case 1:
                    onConnectionConnected();
                    break;

                case MSG_REFRESH:
                {
                    Logger.d("MSG_REFRESH");
                    mLunAddImpl.loadMessage();
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * connected to server
     */
    protected void onConnectionConnected(){
        if (errorItemContainer!=null) {
            errorItemContainer.setVisibility(View.GONE);
        }
    }

    protected void onConnectionDisconnected() {
        if (errorItemContainer!=null && errorText!=null) {
            errorItemContainer.setVisibility(View.VISIBLE);
            if (NetUtils.hasNetwork(getActivity())) {
                errorText.setText(R.string.can_not_connect_chat_server_connection);
            } else {
                errorText.setText(R.string.the_current_network);
            }
        }
    }



    private Notification getNotification(String title, String content) {
        Intent resultIntent = new Intent(getActivity(), LoginActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getActivity(), 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setTicker(content);

        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);


        return builder.build();
    }

    @Override
    public void enableAlarmService(int duration){
        int setTime = duration*60*60 + 30*60;  //Set alarm time in seconds with 30 min delay
        AlarmManager processTimer = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), DurationTimerReceiver.class);
        intent.putExtra(DurationTimerReceiver.MSG,DurationTimerReceiver.CHAT_END);
        intent.putExtra(DurationTimerReceiver.NOTIFICATION_ID,DurationTimerReceiver.NOTIFICATION_1);
        intent.putExtra(DurationTimerReceiver.NOTIFICATION,getNotification(getString(R.string.chat_end_title),getString(R.string.chat_end_content)));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0,  intent, PendingIntent.FLAG_UPDATE_CURRENT);
        processTimer.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+setTime*1000, pendingIntent);

    }

    @Override
    public void cancelAlarmService(){
        AlarmManager processTimer = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), DurationTimerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        processTimer.cancel(pendingIntent);
    }



    public void getRecyclerViewDuration() {
        if (durationSelector != null) {
            durationSelector.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setDurationValue();
                }
            }, 300);
            /*
            durationSelector.postDelayed(new Runnable() {
                @Override
                public void run() {
                    durationSelector.smoothScrollToPosition(mDurationAdapter.getItemCount()-1);
                    setDurationValue();
                }
            }, 5000);
            */
        }
        vtoDate = durationSelector.getViewTreeObserver();
        vtoDate.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                durationSelector.getViewTreeObserver().removeOnPreDrawListener(this);
                finalWidth = durationSelector.getMeasuredWidth();
                itemWidth = getResources().getDimension(R.dimen.item_Width);
                padding = (finalWidth - itemWidth) / 2;
                firstItemWidth = padding ;
                allPixelsDate = 0;

                final LinearLayoutManager durationLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                durationLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                durationSelector.setLayoutManager(durationLayoutManager);
                durationSelector.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        synchronized (this) {
                            if(newState == RecyclerView.SCROLL_STATE_IDLE){
                                calculatePositionAndScrollDate(recyclerView);
                            }
                        }

                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        allPixelsDate += dx;
                    }
                });
                if (durationList==null){
                    durationList = new DurationList();
                }
                mDurationAdapter = new DurationAdapter(durationList.getDurationList(),(int) firstItemWidth,getActivity());
                durationSelector.setAdapter(mDurationAdapter);
                mDurationAdapter.setSelecteditem(mDurationAdapter.getItemCount() - 1);
                return true;
            }
        });
    }
/* this if most important, if expectedPositionDate < 0 recyclerView will return to nearest item*/

    private void calculatePositionAndScrollDate(RecyclerView recyclerView) {
        int expectedPositionDate = Math.round((allPixelsDate + padding - firstItemWidth) / itemWidth);

        if (expectedPositionDate == -1) {
            expectedPositionDate = 0;
        } else if (expectedPositionDate >= recyclerView.getAdapter().getItemCount() - 2) {
            expectedPositionDate--;
        }
        scrollListToPositionDate(recyclerView, expectedPositionDate);
    }
    /* this if most important, if expectedPositionDate < 0 recyclerView will return to nearest item*/
    private void scrollListToPositionDate(RecyclerView recyclerView, int expectedPositionDate) {
        float targetScrollPosDate = expectedPositionDate * itemWidth + firstItemWidth - padding;
        float missingPxDate = targetScrollPosDate - allPixelsDate;
        if (missingPxDate != 0) {
            recyclerView.smoothScrollBy((int) missingPxDate, 0);
        }
        setDurationValue();
    }

    private void setDurationValue() {
        int expectedPositionDurationColor = Math.round((allPixelsDate + padding - firstItemWidth) / itemWidth);
        int setColorDuration = expectedPositionDurationColor + 1;
        //set color here
        mDurationAdapter.setSelecteditem(setColorDuration);
    }



}
