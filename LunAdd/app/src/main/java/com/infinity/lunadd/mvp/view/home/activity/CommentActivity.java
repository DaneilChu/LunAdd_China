package com.infinity.lunadd.mvp.view.home.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.domain.UserDao;
import com.hyphenate.easeui.model.EaseDefaultEmojiconDatas;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenuBase;
import com.infinity.lunadd.mvp.view.home.ICommentView;
import com.jaeger.library.StatusBarUtil;
import com.infinity.lunadd.R;
import com.infinity.lunadd.adapter.CommentAdapter;
import com.infinity.lunadd.base.BaseActivity;
import com.infinity.lunadd.event.CommentsEvent;
import com.infinity.lunadd.event.EventConstant;
import com.infinity.lunadd.mvp.model.bean.Comment;
import com.infinity.lunadd.mvp.model.bean.Post;
import com.infinity.lunadd.mvp.model.rx.RxBus;
import com.infinity.lunadd.mvp.presenter.home.impl.CommentPresenterImpl;
import com.infinity.lunadd.util.OnItemClickListener;
import com.infinity.lunadd.util.OnItemLongClickListener;
import com.orhanobut.logger.Logger;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by DanielChu on 2017/7/23.
 */
public class CommentActivity extends BaseActivity implements ICommentView, SwipeRefreshLayout.OnRefreshListener, OnItemLongClickListener, OnItemClickListener {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.ry_comment)
    RecyclerView mRyComment;
    @BindView(R.id.swipeFreshLayout)
    SwipeRefreshLayout mSwipeFreshLayout;
    @BindView(R.id.et_comment)
    EditText mEtComment;
    @BindView(R.id.btn_send)
    Button mBtnSend;
    @BindView(R.id.txt_time_comment)
    TextView mTime;
    @BindView(R.id.tv_author_comment)
    TextView mAuthor;
    @BindView(R.id.tv_content_comment)
    TextView mContent;
    @BindView(R.id.iv_post_pic_comment)
    ImageView mPic;
    boolean isReplied;
    @Inject
    CommentPresenterImpl mCommentPresenter;
    @Inject
    RxBus mRxBus;
    @BindView(R.id.emojicon_menu_container)
    FrameLayout emojiconMenuContainer;

    private Subscription mSubscription;
    final int size = 15;
    int page;
    private Post mPost;
    private List<Comment> mList;
    private CommentAdapter mAdapter;
    private Comment mComment;
    private int position;
    private InputMethodManager inputMethodManager;
    protected EaseEmojiconMenuBase emojiconMenu;
    protected LayoutInflater layoutInflater;
    private final Context mContext = this;
    private final Handler handler = new Handler();



    @Override
    protected void initData() {
        mPost = (Post) getIntent().getExtras().get("post");
        /*
        String postId = getIntent().getExtras().getString("post");
        try {
            mPost = AVObject.createWithoutData(Post.class, postId);
        } catch (AVException e) {
            e.printStackTrace();
        }
        */
        position = getIntent().getExtras().getInt("position");
        mCommentPresenter.fetchAllComments(mPost, page, size);
        mSubscription = registerEvent();
        mComment = new Comment();
        mComment.setPost(mPost);
        loadPost(mPost,getIntent().getExtras().getString("imgurl"));
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    }

    private void loadPost(final Post post, final String imgUrl){
        Spannable span = EaseSmileUtils.getSmiledText(mContext, post.getContent());
        mContent.setText(span, TextView.BufferType.SPANNABLE);
        mAuthor.setText(post.getAuthor().getString(UserDao.NICK));
        mTime.setText(com.hyphenate.util.DateUtils.getTimestampString(post.getCreatedAt()));
        Glide.with(this).load(imgUrl).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(mPic);
        mPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailImageActivity.class);
                ArrayList<String> list = new ArrayList<>();
                list.add(imgUrl);
                intent.putStringArrayListExtra("imgurl", list);
                intent.putExtra("index", 0);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, v, imgUrl);
                mContext.startActivity(intent, optionsCompat.toBundle());
            }
        });
    }

    @Override
    public void initDagger() {
        mActivityComponent.inject(this);
    }

    @Override
    public void initContentView() {
        setContentView(R.layout.activity_comment);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
        layoutInflater = LayoutInflater.from(this);
    }

    private Subscription registerEvent() {
        return mRxBus.toObservable(CommentsEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CommentsEvent>() {
                    @Override
                    public void call(CommentsEvent event) {
                        switch (event.mAction) {
                            case EventConstant.REFRESH:
                                mList.clear();
                                mList.addAll(event.mCommentList);
                                break;
                            case EventConstant.LOADMORE:
                                mList.addAll(event.mCommentList);
                                break;
                            case EventConstant.DELETE:
                                mList.remove(event.mComment);
                                break;
                            case EventConstant.ADD:
                                mList.add(0, event.mComment);
                                break;
                            default:
                                break;
                        }
                        mComment = new Comment();
                        mComment.setPost(mPost);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }


    @Override
    public void initViewsAndListener() {
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRyComment.setLayoutManager(mLinearLayoutManager);
        mRyComment.setItemAnimator(new FadeInUpAnimator());
        mRyComment.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        mSwipeFreshLayout.setOnRefreshListener(this);
        mSwipeFreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mList = new ArrayList<>();
        mAdapter = new CommentAdapter(mList, this);

        mRyComment.setAdapter(mAdapter);
        mEtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(mEtComment.getText().toString())) {
                    mBtnSend.setEnabled(false);
                } else {
                    mBtnSend.setEnabled(true);
                }
            }
        });
        mAdapter.setItemOnclicklistener(this);
        mAdapter.setItemOnLongClickListener(this);
        initializeEmoji();
    }

    private void initializeEmoji(){
        if(emojiconMenu == null){
            emojiconMenu = (EaseEmojiconMenu) layoutInflater.inflate(com.hyphenate.easeui.R.layout.ease_layout_emojicon_menu, null);
            List<EaseEmojiconGroupEntity>  emojiconGroupList = new ArrayList<>();
            emojiconGroupList.add(new EaseEmojiconGroupEntity(com.hyphenate.easeui.R.drawable.e1f60a2,  Arrays.asList(EaseDefaultEmojiconDatas.getData())));
            ((EaseEmojiconMenu)emojiconMenu).init(emojiconGroupList);
        }
        emojiconMenuContainer.addView(emojiconMenu);
        emojiconMenu.setVisibility(View.GONE);


        // emojicon menu
        emojiconMenu.setEmojiconMenuListener(new EaseEmojiconMenuBase.EaseEmojiconMenuListener() {

            @Override
            public void onExpressionClicked(EaseEmojicon emojicon) {
                if(emojicon.getEmojiText() != null){
                    onEmojiconInputEvent(EaseSmileUtils.getSmiledText(mContext,emojicon.getEmojiText()));
                }
            }

            @Override
            public void onDeleteImageClicked() {
                onEmojiconDeleteEvent();
            }
        });
    }

    /**
     * append emoji icon to editText
     * @param emojiContent
     */
    public void onEmojiconInputEvent(CharSequence emojiContent){
        mEtComment.append(emojiContent);
    }

    /**
     * delete emojicon
     */
    public void onEmojiconDeleteEvent(){
        if (!TextUtils.isEmpty(mEtComment.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            mEtComment.dispatchKeyEvent(event);
        }
    }

    /**
     * 显示或隐藏表情页
     */
    @OnClick(R.id.iv_emoji)
    protected void toggleEmojicon() {
        if (emojiconMenu.getVisibility() == View.GONE) {
            hideKeyboard();
            handler.postDelayed(new Runnable() {
                public void run() {
                    emojiconMenu.setVisibility(View.VISIBLE);
                }
            }, 50);
        } else {
            emojiconMenu.setVisibility(View.GONE);

        }
    }

    private void hideKeyboard() {
        Logger.d("hideKeyboard");
        if (getWindow().getAttributes().softInputMode !=
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN && getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void initPresenter() {
        mCommentPresenter.attachView(this);
    }

    @Override
    public void onBackPressed() {
        if (emojiconMenu.getVisibility() != View.GONE){
            emojiconMenu.setVisibility(View.GONE);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void initToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void showLoading() {
        if (mSwipeFreshLayout!=null)
        mSwipeFreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeFreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void hideLoading() {
        if (mSwipeFreshLayout != null)
            mSwipeFreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeFreshLayout.setRefreshing(false);
                }
            }, 1000);
    }


    @OnClick(R.id.btn_send)
    public void onButtonSendClick() {
        mComment.setComment(mEtComment.getText().toString());
        mComment.setAuhtor(User.getCurrentUser(User.class));
        mCommentPresenter.Comment(mComment, position);
        mEtComment.setText("");
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCommentPresenter.detachView();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void onRefresh() {
        mCommentPresenter.fetchAllComments(mPost, 0, size);
    }

    @Override
    public void onClick(View view, int position) {
        if (mList.get(position).getAuhtor().equals(User.getCurrentUser(User.class))) {
            showToast("不自己回复自己");
            return;
        }
        mEtComment.setHint(String.format("回复 %s 的评论:", mList.get(position).getAuhtor().getNick()));
        mEtComment.requestFocus();

        inputMethodManager.showSoftInput(mEtComment, InputMethodManager.RESULT_UNCHANGED_SHOWN);
        mComment.setReply(mList.get(position).getAuhtor());
        isReplied = true;
    }

    @OnClick(R.id.et_comment)
    public void onCommentClick(){
        if (emojiconMenu.getVisibility() != View.GONE){
            emojiconMenu.setVisibility(View.GONE);
            inputMethodManager.showSoftInput(mEtComment, InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

    @Override
    public void onLongClick(View view, int position) {
        final Comment comment = mList.get(position);
        showWarningDialog("删除", "确定要删除评论吗？", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                mCommentPresenter.deleteComment(comment);
            }
        });
    }
}
