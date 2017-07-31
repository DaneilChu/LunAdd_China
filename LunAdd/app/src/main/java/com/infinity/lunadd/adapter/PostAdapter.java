package com.infinity.lunadd.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.domain.UserDao;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.orhanobut.logger.Logger;
import com.infinity.lunadd.R;
import com.infinity.lunadd.mvp.model.bean.Post;
import com.infinity.lunadd.mvp.view.home.activity.CommentActivity;
import com.infinity.lunadd.mvp.view.home.activity.DetailImageActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DanielChu on 2017/7/21.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    final List<Post> mList;
    final Context mContext;

    public PostAdapter(@NonNull List<Post> List, Context context) {
        this.mList = List;
        this.mContext = context;
        Logger.init(this.getClass().getSimpleName());
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_fragment, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Spannable span = EaseSmileUtils.getSmiledText(mContext, mList.get(position).getContent());
        holder.tvContent.setText(span, TextView.BufferType.SPANNABLE);
        holder.tvAuthor.setText(mList.get(position).getAuthor().getString(UserDao.NICK));
        holder.txtTime.setText(com.hyphenate.util.DateUtils.getTimestampString(mList.get(position).getCreatedAt()));
        holder.tv_comment.setText(String.valueOf(mList.get(position).getCommentcount()));
        holder.tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("post", mList.get(holder.getAdapterPosition()));
                intent.putExtra("imgurl", mList.get(holder.getAdapterPosition()).getPicture());
                intent.putExtra("position", holder.getAdapterPosition());
                mContext.startActivity(intent);
            }
        });
        Glide.with(mContext).load(mList.get(position).getPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(holder.ivPostPic);
        Logger.d(mList.get(position).getPicture());
        holder.ivPostPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailImageActivity.class);
                ArrayList<String> list = new ArrayList<>();
                list.add(mList.get(holder.getAdapterPosition()).getPicture());
                intent.putStringArrayListExtra("imgurl", list);
                intent.putExtra("index", holder.getAdapterPosition());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, v, mList.get(holder.getAdapterPosition()).getPicture());
                mContext.startActivity(intent, optionsCompat.toBundle());
            }
        });
    }

    private boolean isMyPost(int position) {
        return mList.get(position).getAuthorId().equals(AVUser.getCurrentUser().getObjectId());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.iv_post_pic)
        ImageView ivPostPic;
        @BindView(R.id.txt_time)
        TextView txtTime;
        @BindView(R.id.tv_author)
        TextView tvAuthor;
        @BindView(R.id.tv_comment)
        TextView tv_comment;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }

}
