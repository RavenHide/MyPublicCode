package com.example.myrecyclerview.commonAdapter;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Zhaoyouqing on 2017/7/22.
 */

public abstract class QuickAdapter<T> extends RecyclerView.Adapter<QuickAdapter.VH> {
    private List<T> mDatas;

    public QuickAdapter(List<T> mDatas) {
        this.mDatas = mDatas;
    }

    /**
     * 插入数据
     * @param mDatas
     * @param startPosition 插入的位置
     * @param count 插入的数量
     */
    public void insertDatas(List<T> mDatas, int startPosition, int count){
        this.mDatas = mDatas;
        notifyItemRangeChanged(startPosition, count);
    }
    /**
     * 根据position来返回相应的布局I
     *
     * @param viewType
     * @return
     */
    public abstract int getLayoutId(int viewType);

    /**
     * @param parent
     * @param viewType 如果不重写getItemViewType(position)方法，这里的viewtype就是只position
     * @return
     */
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return VH.getVH(parent, getLayoutId(viewType));
    }

    @Override
    public void onBindViewHolder(final VH holder, int position) {
//        if(position == mDatas.size()){
//             convert(holder, null, position);
//        }else if(position < mDatas.size()){
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.itemView.getContext(), " xxxxxx", Toast.LENGTH_SHORT).show();
            }
        });
            convert(holder, mDatas.get(position), position);
//        }

    }
    public abstract void convert(VH holder,   T data, int position);

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        private SparseArrayCompat<View> mViews;//存储itemview的childview
        private View mConcertView;//itemView

        public VH(View itemView) {
            super(itemView);
            mConcertView = itemView;
            mViews = new SparseArrayCompat<>();
        }

        /**
         * 返回一个viewholder， 用于adpater的oncreatedVuew
         *
         * @param parent   viewholder的父类，这里多指recyclerview
         * @param layoutId viewholder的布局id
         * @return
         */
        public static VH getVH(ViewGroup parent, int layoutId) {
            View convertView = LayoutInflater.from(parent.getContext()).inflate(layoutId,
                    parent, false);
            return new VH(convertView);
        }

        public <T extends View> T getView(int viewId) {
            View childView = mViews.get(viewId);
            if (childView == null) {
                childView = mConcertView.findViewById(viewId);
                mViews.put(viewId, childView);
            }
            return (T) childView;
        }

        /**
         * TextView 的 setText
         * 用于对childview进行一些常用设置，可以根据自己的需要来添加
         */
        public void setText(int viewId, String value) {
            TextView textView = getView(viewId);
            textView.setText(value);
        }

        /**
         * 设置progressbar是否可见：View.GONE View.VISIBLE View.INVISIBLE
         * @param viewID
         * @param visibleState
         */
        public void setProgressBarVisible(int viewID, int visibleState){
            ProgressBar progressBar = getView(viewID);
            progressBar.setVisibility(visibleState);
        }

        public void setTextViewVisible(int viewId, int visibleState) {
            TextView textView = getView(viewId);
            textView.setVisibility(visibleState);
        }
    }
}
