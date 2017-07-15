package com.example.zhaoyouqing.doublerecycleview.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.zhaoyouqing.doublerecycleview.R;
import com.example.zhaoyouqing.doublerecycleview.holder.RvHolder;
import com.example.zhaoyouqing.doublerecycleview.interfaces.Rvlistener;

import java.util.List;

/**
 * Created by Zhaoyouqing on 2017/7/15.
 */

public class SortAdapter extends RvAdapter<String> {

    private int checkedPosition;

    public SortAdapter(Context context, List list, Rvlistener listener) {
        super(context, list, listener);
    }

    public void setCheckedPosition(int position){
        this.checkedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_sort_list;
    }

    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new SortHolder(view, viewType, listener);
    }

    private class SortHolder extends RvHolder<String>{

        private TextView tv_name;
        private View mView;

        public SortHolder(View itemView, int type, Rvlistener listener) {
            super(itemView, type, listener);
            this.mView = itemView;
            tv_name = (TextView) mView.findViewById(R.id.tv_sort);
        }

        @Override
        public void bindHolder(String s, int position) {
            tv_name.setText("数据" + s);
            if(position == checkedPosition){
                mView.setBackgroundColor(Color.parseColor("#f3f3f3"));
                tv_name.setTextColor(Color.parseColor("#0068cf"));
            }else {
                mView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                tv_name.setTextColor(Color.parseColor("#1e1d1d"));
            }
        }
    }
}
