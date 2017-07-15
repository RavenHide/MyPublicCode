package com.example.zhaoyouqing.doublerecycleview.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.zhaoyouqing.doublerecycleview.interfaces.Rvlistener;

/**
 * Created by Zhaoyouqing on 2017/7/14.
 */

public abstract class RvHolder<T> extends RecyclerView.ViewHolder {
    protected Rvlistener mListener;

    public RvHolder(View itemView, int type, Rvlistener listener) {
        super(itemView);
        this.mListener = listener;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(view.getId(), getAdapterPosition());
            }
        });
    }

    public abstract void bindHolder(T t, int position);
}
