package com.example.myrecyclerview.commonAdapter;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Zhaoyouqing on 2017/7/23.
 */

public abstract class HeaderAndFooterWrapper<T> extends QuickAdapter<T> {
    private static final int BASE_ITEM_TYPE_HEADER = 1000;
    private static final int BASE_ITEM_TYPE_FOOTER = 2000;

    private SparseArrayCompat<View> mHeaderViews;
    private SparseArrayCompat<View> mFooterViews;

    public HeaderAndFooterWrapper(List<T> mDatas) {
        super(mDatas);
        mHeaderViews = new SparseArrayCompat<>();
        mFooterViews = new SparseArrayCompat<>();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
//        Log.d(TAG, "onCreateViewHolder: " + viewType);
        if(mHeaderViews.get(viewType) != null){
            return new VH(mHeaderViews.get(viewType));
        }else if(mFooterViews.get(viewType) != null){
            return new VH(mFooterViews.get(viewType));
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if(isHeaderViewsPos(position)){
            return mHeaderViews.keyAt(position);
        }else if(isFooterViewPos(position)){
            return mFooterViews.keyAt(position - mHeaderViews.size() - super.getItemCount());
        }else {
            return getBodyItemViewType(position);
        }

    }

    /**
     * 返回body主体的itemview类型
     * @param position
     * @return
     */
    public abstract int getBodyItemViewType(int position);
    @Override
    public void onBindViewHolder(VH holder, int position) {
        if(isHeaderViewsPos(position) || isFooterViewPos(position)){
            return ;
        }
        super.onBindViewHolder(holder, position - mHeaderViews.size());
    }

    private boolean isHeaderViewsPos(int position){
        return position < mHeaderViews.size();
    }
    private boolean isFooterViewPos(int position){
        return position >= mHeaderViews.size() +   super.getItemCount();
    }
    public void addHeaderView(View view){
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }
    public void addFooterView(View view){
        mFooterViews.put(mFooterViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    @Override
    public int getItemCount() {
        return mFooterViews.size() + mHeaderViews.size() +  super.getItemCount();
    }

    public int getmHeaderViewsCount() {
        return mHeaderViews.size();
    }

    public int getmFooterViewsCount() {
        return mFooterViews.size();
    }

    public View getHeaderViews(int position) {
        View view = mHeaderViews.get(position + BASE_ITEM_TYPE_HEADER);
        return  view != null ? view: null;
    }

    public View getFooterViews(int position) {
        View view = mFooterViews.get(position + BASE_ITEM_TYPE_FOOTER);
        return  view != null ? view: null;
    }

    @Override
    public void onViewAttachedToWindow(VH holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if(isHeaderViewsPos(position) || isFooterViewPos(position)){
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if(layoutParams instanceof StaggeredGridLayoutManager.LayoutParams){
                StaggeredGridLayoutManager.LayoutParams layoutParams1 = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                layoutParams1.setFullSpan(true);
                holder.itemView.setLayoutParams(layoutParams1);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if(mHeaderViews.get(viewType) != null){
                        return gridLayoutManager.getSpanCount();
                    }else if(mFooterViews.get(viewType) != null){
                        return gridLayoutManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }

    }
}
