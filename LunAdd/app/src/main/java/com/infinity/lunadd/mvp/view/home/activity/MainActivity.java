package com.infinity.lunadd.mvp.view.home.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentContainer;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.domain.UserDao;
import com.infinity.lunadd.EaseUIHelper;
import com.infinity.lunadd.R;
import com.infinity.lunadd.base.BaseActivity;
import com.infinity.lunadd.mvp.presenter.home.impl.MainImpl;
import com.infinity.lunadd.mvp.view.home.IMainView;
import com.infinity.lunadd.mvp.view.home.fragment.LunAddFragment;
import com.infinity.lunadd.mvp.view.home.fragment.ProfileFragment;
import com.infinity.lunadd.mvp.view.home.fragment.WallFragment;
import com.jaeger.library.StatusBarUtil;
import com.orhanobut.logger.Logger;


import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;


/**
 * Created by DanielChu on 2017/6/14.
 */
public class MainActivity extends BaseActivity implements IMainView {
    @Inject
    MainImpl mMainImpl;
    @Inject
    EaseUIHelper mEaseUiHelper;
    @BindView(R.id.unread_msg_number)
    TextView unreaMsgdLabel;
    @BindView(R.id.unread_profile_number)
    TextView unreadProfileLabel;
    @BindView(R.id.iv_lunadd)
    ImageView imagebuttons_lunadd;
    @BindView(R.id.iv_wall)
    ImageView imagebuttons_wall;
    @BindView(R.id.iv_profile)
    ImageView imagebuttons_profile;
    @BindView(R.id.tv_lunadd)
    TextView textView_lunadd;
    @BindView(R.id.tv_wall)
    TextView textView_wall;
    @BindView(R.id.tv_profile)
    TextView textView_profile;
    LunAddFragment lunAddFragment;
    ProfileFragment profileFragment;
    WallFragment wallFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;// 当前fragment的index
    private ImageView[] imagebuttons;
    private TextView[] textviews;



    @Override
    protected void initData() {
        updateUnreadLabel();
    }

    @Override
    protected void onResume(){
        super.onResume();

        updateUnreadLabel();

        // unregister this event listener when this activity enters the
        // background
        mEaseUiHelper.pushActivity(this);

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    @Override
    protected void onStop() {
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        mEaseUiHelper.popActivity(this);
        super.onStop();
    }


    @Override
    public void initDagger() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initContentView() {
        setContentView(R.layout.activity_main);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));

    }

    @Override
    public void initViewsAndListener() {
        initTabView();
    }

    private void initTabView() {
        imagebuttons_lunadd.setSelected(true);
        textView_lunadd.setTextColor(0xFF45C01A);
        lunAddFragment = LunAddFragment.newInstance();
        profileFragment = ProfileFragment.newInstance();
        wallFragment = WallFragment.newInstance();
        fragments = new Fragment[] { lunAddFragment,wallFragment, profileFragment};
        imagebuttons = new ImageView[]{imagebuttons_lunadd,imagebuttons_wall,imagebuttons_profile};
        textviews = new TextView[]{textView_lunadd,textView_wall,textView_profile};
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, lunAddFragment)
                .add(R.id.fragment_container, wallFragment)
                .add(R.id.fragment_container, profileFragment)
                .hide(profileFragment).hide(wallFragment).show(lunAddFragment).commit();
    }

    /**
     * 获取未读消息数
     */
    public void updateUnreadLabel() {
        Logger.d("updateUnreadLabel");
        int count = mMainImpl.getUnread();
        if (count > 0) {
            unreaMsgdLabel.setText(String.valueOf(count));
            unreaMsgdLabel.setVisibility(View.VISIBLE);
        } else {
            unreaMsgdLabel.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * clear unread message
     */
    public void clearUnreadLabel() {
        unreaMsgdLabel.setVisibility(View.INVISIBLE);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void initPresenter() {
        mMainImpl.attachView(this);
    }

    @Override
    public void initToolbar() {
    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.re_lunadd:
                index = 0;
                break;
            case R.id.re_wall:
                index = 1;
                break;
            case R.id.re_profile:
                index = 2;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        imagebuttons[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        imagebuttons[index].setSelected(true);
        textviews[currentTabIndex].setTextColor(0xFF999999);
        textviews[index].setTextColor(0xFF45C01A);
        currentTabIndex = index;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mMainImpl.detachView();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // notify new message
            if (currentTabIndex!=0) {
                for (EMMessage message : messages) {
                    if (message.getUserName()== AVUser.getCurrentUser().get(UserDao.OTHERUSERID)) {
                        mEaseUiHelper.getNotifier().onNewMsg(message);
                    }
                }
            }
            refreshUIWithMessage();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {

        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {}
    };

    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // refresh unread count
                updateUnreadLabel();
                if (currentTabIndex == 0) {
                    // refresh conversation list
                    if (lunAddFragment != null) {
                        lunAddFragment.refreshMessage();
                    }
                }
            }
        });
    }




}
