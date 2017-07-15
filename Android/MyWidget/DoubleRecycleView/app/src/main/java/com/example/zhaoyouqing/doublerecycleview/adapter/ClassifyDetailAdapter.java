package com.example.zhaoyouqing.doublerecycleview.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhaoyouqing.doublerecycleview.R;
import com.example.zhaoyouqing.doublerecycleview.bean.SortBean;
import com.example.zhaoyouqing.doublerecycleview.holder.RvHolder;
import com.example.zhaoyouqing.doublerecycleview.interfaces.Rvlistener;

import java.util.List;

/**
 * Created by Zhaoyouqing on 2017/7/14.
 */

public class ClassifyDetailAdapter extends RvAdapter<SortBean> {

    private final static int TITLE = 0;
    private final static int ITEM = 1;
    public ClassifyDetailAdapter(Context context, List<SortBean> list, Rvlistener listener) {
        super(context, list, listener);
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).isTitle()? TITLE: ITEM;
    }

    @Override
    protected int getLayoutId(int viewType) {
        return viewType ==  TITLE? R.layout.item_title: R.layout.item_classify_detail;
    }

    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new ClassifyHolder(view, viewType, listener);
    }

    public class ClassifyHolder extends RvHolder<SortBean>{
        TextView tv_City;
        ImageView avatar;
        TextView tv_title;

        public ClassifyHolder(View itemView, int type, Rvlistener listener) {
            super(itemView, type, listener);
            switch (type){
                case 0:
                    tv_title = (TextView) itemView.findViewById(R.id.tv_title);
                    break;
                case 1:
                    tv_City = (TextView) itemView.findViewById(R.id.tvCity);
                    avatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
                    break;
            }
        }

        @Override
        public void bindHolder(SortBean sortBean, int position) {
            int itemViewType = ClassifyDetailAdapter.this.getItemViewType(position);
            switch (itemViewType){
                case 0:
                    tv_title.setText("测试数据" + sortBean.getTag());
                    break;
                case 1:
                    tv_City.setText(sortBean.getName());
                    break;
            }
        }
    }
}
