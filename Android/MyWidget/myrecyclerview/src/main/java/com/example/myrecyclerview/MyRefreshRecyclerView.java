package com.example.myrecyclerview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.myrecyclerview.commonAdapter.HeaderAndFooterWrapper;
import com.example.myrecyclerview.statueCallBack.BaseStatueCallBack;

/**
 * Created by Zhaoyouqing on 2017/7/24.
 */

public class MyRefreshRecyclerView extends MyLoadRecyclerView {
    private static final String TAG = "MyRefreshRecyclerView";
    private View mRefreshView; // 下拉刷新的头部View
    private int mRefreshViewHeight; // 下拉刷新头部的高度

    public MyRefreshRecyclerView(Context context) {
        this(context, null, 0);
    }

    public MyRefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        HeaderAndFooterWrapper headerAndFooterWrapper = (HeaderAndFooterWrapper) adapter;
        mRefreshView = headerAndFooterWrapper.getHeaderViews(0);
        super.setAdapter(adapter);
    }

    /**
     * 做下拉刷新相关
     * @param e
     */
    @Override
    protected void doRefreshJob(MotionEvent e) {
        super.clearAnimator();
        // 解决下拉刷新自动滚动问题
        if (mCurrentDrag) {
            scrollToPosition(0);
        }
        int offsetY = (int) ((e.getRawY() - mFingerDownY) * mDragIndex);
        if (offsetY > 0) {//如果是下拉
            int martinTop = offsetY - mRefreshViewHeight;
            updateRefreshStatueByOffset(offsetY);
            setRefreshViewMarginTop(martinTop);
            mCurrentDrag = true;
        }
    }

    @Override
    protected void setRefreshViewMarginTop(int marginTop) {
        if (marginTop < -mRefreshViewHeight + 1) {
            //限制marginTop的最小值为-mRefreshViewHeight + 1
            marginTop = -mRefreshViewHeight + 1;
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mRefreshView.getLayoutParams();
        params.topMargin = marginTop;
        mRefreshView.setLayoutParams(params);
    }

    @Override
    protected void restoreRefreshView() {
        int currentMarginTop = ((ViewGroup.MarginLayoutParams) mRefreshView.getLayoutParams()).topMargin;
        int finalMarginTop = -mRefreshViewHeight + 1;
        if (mCurrentRefreshStatus == REFRESH_STATUE_RELEASE
                || mCurrentRefreshStatus == REFRESH_STATUE_REFRESHING) {
            finalMarginTop = 0;
        }
        super.initAnimatorAndStart(currentMarginTop, finalMarginTop, true);
    }


    /**
     * 判断是否可以向下滚动
     * @return
     */
    @Override
    public boolean canScrollDown() {
        if (Build.VERSION.SDK_INT < 14) {
            return canScrollVertically(-1) || this.getScrollY() > 0;
        } else {
            return canScrollVertically(-1);
        }
    }

    /**
     * 刷新后回调
     * @param str
     */
    @Override
    public void refreshOver(String str) {
        mCurrentRefreshStatus = REFRESH_STATUE_OVER;
        refreshStatueCallBack.callBack(mCurrentRefreshStatus, str);
        restoreRefreshView();
    }

    @Override
    protected void measureRefreshViewHeight() {
        if (mRefreshView != null && mRefreshViewHeight <= 0) {
            mRefreshViewHeight = mRefreshView.getMeasuredHeight();
            if (mRefreshViewHeight > 0) {
                //将刷新view隐藏到屏幕上方
                setRefreshViewMarginTop(-mRefreshViewHeight + 1);
            }
        }
    }
    @Override
    public void setRefreshCallBack(BaseStatueCallBack callBack){
        this.refreshStatueCallBack = callBack;
    }


    @Override
    protected void updateRefreshStatueByOffset(int offset) {
        if(mCurrentRefreshStatus ==  REFRESH_STATUE_REFRESHING ||
                mCurrentRefreshStatus == REFRESH_STATUE_OVER){
            return;
        }

        if (offset <= 0) {
            mCurrentRefreshStatus = REFRESH_STATUE_NORMAL;
        } else if (offset > 0 && offset < mRefreshViewHeight) {
            mCurrentRefreshStatus = REFRESH_STATUE_PULL_DOWN;;
        } else if (offset >= mRefreshViewHeight) {
            mCurrentRefreshStatus = REFRESH_STATUE_RELEASE;
        }
        refreshStatueCallBack.callBack(mCurrentRefreshStatus, "");
    }
}
