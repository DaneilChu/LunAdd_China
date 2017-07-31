package com.infinity.lunadd.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.infinity.lunadd.R;
import com.infinity.lunadd.mvp.model.bean.DurationList;
import com.infinity.lunadd.mvp.model.bean.News;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DanielChu on 2017/6/25.
 */
public class DurationAdapter extends RecyclerView.Adapter<DurationAdapter.DurationViewHolder>  {
    private ArrayList<Integer> durationList;
    private static final int VIEW_TYPE_ITEM = 2;
    private static final int VIEW_TYPE_PADDING = 1;
    private int selectedItem = -1;
    private int paddingWidthDate = 0;
    final Context mContext;

    public DurationAdapter(ArrayList<Integer> durationList,int paddingWidthDate, Context context) {
        this.durationList = durationList;
        this.paddingWidthDate = paddingWidthDate;
        this.mContext = context;
    }

    @Override
    public DurationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,
                    parent, false);
            return new DurationViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,
                    parent, false);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.width = paddingWidthDate;
            view.setLayoutParams(layoutParams);
            return new DurationViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return durationList.size();
    }

    @Override
    public void onBindViewHolder(DurationViewHolder holder, int position) {
        int duration = durationList.get(position);
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {

            holder.mTvValue.setText(String.valueOf(duration));
            holder.mTvValue.setVisibility(View.VISIBLE);

            if (position == selectedItem) {
                holder.mTvValue.setTextColor(ContextCompat.getColor(mContext,R.color.duration_value_on));
                holder.mTvValue.setTextSize(50);
                holder.mTvBgr.setBackgroundResource(R.color.colorWhite);

            } else {
                holder.mTvValue.setTextColor(ContextCompat.getColor(mContext,R.color.duration_value_off));
                holder.mTvValue.setTextSize(25);
                holder.mTvBgr.setBackgroundResource(R.color.colorWhite);
            }
        }else {
            holder.mTvValue.setVisibility(View.INVISIBLE);
            holder.mTvBgr.setBackgroundResource(R.color.colorWhite);
        }
    }

    public void setSelecteditem(int selecteditem) {
        this.selectedItem = selecteditem;
        notifyDataSetChanged();
    }

    public int getSelecteditem() {
        return selectedItem;
    }

    @Override
    public int getItemViewType(int position) {
        int number = durationList.get(position);
        if (number==0||number==100){
            return VIEW_TYPE_PADDING;
        }else {
            return VIEW_TYPE_ITEM;
        }
    }


    class DurationViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.duration_item_text)
        TextView mTvValue;
        @BindView(R.id.duration_bgr)
        FrameLayout mTvBgr;


        public DurationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}