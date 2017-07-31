package com.infinity.lunadd.mvp.view.home.fragment;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.avos.avoscloud.AVAnalytics;
import com.hyphenate.chat.EMClient;
import com.infinity.lunadd.event.PostEvent;
import com.infinity.lunadd.mvp.view.home.IWallView;
import com.orhanobut.logger.Logger;
import com.infinity.lunadd.R;
import com.infinity.lunadd.adapter.PostAdapter;
import com.infinity.lunadd.base.BaseFragment;
import com.infinity.lunadd.event.EventConstant;
import com.infinity.lunadd.event.IncrementEvent;
import com.infinity.lunadd.mvp.model.bean.Post;
import com.infinity.lunadd.mvp.model.rx.RxBus;
import com.infinity.lunadd.mvp.presenter.home.impl.WallPresenterImpl;
import com.infinity.lunadd.mvp.view.home.activity.AddPostActivity;

import java.util.ArrayList;
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
 * Created by DanielChu on 2017/7/21.
 */
public class WallFragment extends BaseFragment implements IWallView, SwipeRefreshLayout.OnRefreshListener {
    @Inject
    WallPresenterImpl mWallPresenterImpl;
    @Inject
    RxBus mRxBus;
    @BindView(R.id.ry_post)
    RecyclerView ryPost;
    @BindView(R.id.swipeFreshLayout)
    SwipeRefreshLayout swipeFreshLayout;
    Subscription mSubscription;
    Subscription mIncrement;
    PostAdapter mAdapter;
    int page;
    int size = 30;
    LinearLayoutManager layout;
    List<Post> mList = new ArrayList<>();

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_wall;
    }

    @Override
    public void initViews() {
        layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        swipeFreshLayout.setOnRefreshListener(this);
        ryPost.setLayoutManager(layout);
        ryPost.setItemAnimator(new FadeInUpAnimator());
        swipeFreshLayout.setColorSchemeResources(R.color.colorPrimary);
        Logger.init(this.getClass().getSimpleName());
        mAdapter = new PostAdapter(mList, getActivity());
        ryPost.setAdapter(mAdapter);
        /*
        ryPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && layout.findLastVisibleItemPosition() + 1 == mAdapter.getItemCount()) {
                    swipeFreshLayout.setRefreshing(true);
                    page++;
                    mWallPresenterImpl.LoadingDataFromNet(false, size, page);
                }
            }
        });
        */
    }

    @OnClick(R.id.textview_reshresh)
    public void loadNextPage(){
        if (swipeFreshLayout!=null) {
            swipeFreshLayout.setRefreshing(true);
            page++;
            mWallPresenterImpl.LoadingDataFromNet(false, size, page);
        }
    }



    public static WallFragment newInstance() {
        return new WallFragment();

    }

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart("SouvenirFragment");

    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd("SouvenirFragment");
    }

    @Override
    public void initDagger() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void initData() {
        mWallPresenterImpl.LoadingDataFromNet(true, size, 0);
        mSubscription = mRxBus.toObservable(PostEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<PostEvent>() {
                    @Override
                    public void call(PostEvent postEvent) {
                        switch (postEvent.mAction) {
                            case EventConstant.REFRESH:
                                mList.clear();
                                mList.addAll(postEvent.mlist);
                                break;
                            case EventConstant.LOADMORE:
                                mList.addAll(postEvent.mlist);
                                break;
                            case EventConstant.DELETE:
                                mList.remove(postEvent.mPost);
                                break;
                            case EventConstant.ADD:
                                mList.add(0, postEvent.mPost);
                                break;
                            default:
                                break;
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });

        mIncrement = mRxBus.toObservable(IncrementEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<IncrementEvent>() {
                    @Override
                    public void call(IncrementEvent IncrementEvent) {
                        mList.get(IncrementEvent.position).setCommentcount(IncrementEvent.count);
                        mAdapter.notifyItemChanged(IncrementEvent.position);
                    }
                });

    }



    @Override
    public void initPresenter() {
        mWallPresenterImpl.attachView(this);
    }



    @Override
    public void showRefreshingLoading() {
        swipeFreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (swipeFreshLayout != null)
                    swipeFreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void hideRefreshingLoading() {
        if (swipeFreshLayout != null)
            swipeFreshLayout.setRefreshing(false);
    }

    @OnClick(R.id.fab)
    @Override
    public void toAddPostActivity() {
        Intent intent = new Intent(getActivity(), AddPostActivity.class);
        startActivity(intent);
    }


    @Override
    public void onRefresh() {
        mWallPresenterImpl.LoadingDataFromNet(true, size, 0);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        if (mIncrement != null && !mIncrement.isUnsubscribed()) {
            mIncrement.unsubscribe();
        }
        mWallPresenterImpl.detachView();
    }

}
