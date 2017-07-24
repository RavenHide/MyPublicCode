package com.example.myrecyclerview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.example.myrecyclerview.commonAdapter.HeaderAndFooterWrapper;
import com.example.myrecyclerview.listener.RefreshAndLoadingListener;
import com.example.myrecyclerview.statueCallBack.BaseStatueCallBack;

/**
 * Created by Zhaoyouqing on 2017/7/24.
 */

public abstract class MyLoadRecyclerView extends RecyclerView {

    private View mLoadView;//上拉加载的头部view
    private int mLoadViewHieght;//上拉加载头部的高度

    protected int mFingerDownY;// 手指按下的Y位置
    protected float mDragIndex = 0.35f;// 手指拖拽的阻力指数
    protected boolean mCurrentDrag = false;

    protected int mCurrentRefreshStatus = REFRESH_STATUE_NORMAL;   // 当前的状态
    protected int mCurrentLoadingStatus = LOAD_STATUE_NORMAL;//当前加载状态

    private BaseStatueCallBack loadStatueCallBack;
    protected BaseStatueCallBack refreshStatueCallBack;
    // 默认状态
    public static final int REFRESH_STATUE_NORMAL = 0x0011;
    public static final int REFRESH_STATUE_PULL_DOWN = 0x0022;
    public static final int REFRESH_STATUE_RELEASE = 0x0033;
    public static final int REFRESH_STATUE_REFRESHING = 0x0044;
    public static final int REFRESH_STATUE_OVER = 0x0055;

    public static final int LOAD_STATUE_NORMAL = 0x001f;
    public static final int LOAD_STATUE_PULL_UP = 0x002f;
    public static final int LOAD_STATUE_RELEASE = 0x003f;
    public static final int LOAD_STATUE_LOADING = 0x004f;
    public static final int LOAD_STATUE_OVER = 0x005f;

    private ValueAnimator animator;
    private RefreshAndLoadingListener refreshAndLoadingListener;

    public MyLoadRecyclerView(Context context) {
        this(context, null, 0);
    }

    public MyLoadRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLoadRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        HeaderAndFooterWrapper headerAndFooterWrapper = (HeaderAndFooterWrapper) adapter;
        mLoadView = headerAndFooterWrapper.getFooterViews(0);
        super.setAdapter(adapter);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            measureRefreshViewHeight();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            // 记录手指按下的位置 ,之所以写在dispatchTouchEvent那是因为如果我们处理了条目点击事件，
            // 那么就不会进入onTouchEvent里面，所以只能在这里获取
            case MotionEvent.ACTION_DOWN:
                mFingerDownY = (int) ev.getRawY();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            // 如果没有到达最顶端，也就是说还可以向上滚动就什么都不处理
            case MotionEvent.ACTION_MOVE:
                if (!canScrollDown() && mCurrentLoadingStatus == LOAD_STATUE_NORMAL) {
                    doRefreshJob(e);
                    break;
                }
                if (!canScrollUp() && mCurrentRefreshStatus == REFRESH_STATUE_NORMAL) {
                    //测量高度，在onlayout方法里面测出为0，所以选择将它放在这里进行测量
                    measureLoadViewHeight();
                    doLoadJob(e);
                    break;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mCurrentDrag && mCurrentLoadingStatus == LOAD_STATUE_NORMAL) {
                    restoreRefreshView();
                    break;
                }
                if (mCurrentDrag && mCurrentRefreshStatus == REFRESH_STATUE_NORMAL) {
                    restoreLoadView();
                    break;
                }
                break;
        }
        return super.onTouchEvent(e);
    }

    /**
     * 做上拉加载相关
     *
     * @param e
     */
    private void doLoadJob(MotionEvent e) {
        clearAnimator();
        if (mCurrentDrag) {
            scrollToPosition(getAdapter().getItemCount() - 1);
        }
        int offsetY = (int) ((e.getRawY() - mFingerDownY) * mDragIndex);
        if (offsetY < 0) {
            int marginBottom = -offsetY - mLoadViewHieght;
            updateLoadStatueByOffset(offsetY);
            setLoadViewMarginBottom(marginBottom);
            mCurrentDrag = true;
        }
    }

    /**
     * 通过改变loadView的marginBottom，来改变其位置
     * @param marginBottom
     */
    private void setLoadViewMarginBottom(int marginBottom) {
        if (marginBottom < -mLoadViewHieght + 1) {
            marginBottom = -mLoadViewHieght + 1;
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mLoadView.getLayoutParams();
        params.bottomMargin = marginBottom;
        mLoadView.setLayoutParams(params);
    }



    private void restoreLoadView() {
        int currentMarginBottom = ((ViewGroup.MarginLayoutParams) mLoadView.getLayoutParams()).bottomMargin;
        int finalMarginBottom = -mLoadViewHieght + 1;
        if (mCurrentLoadingStatus == LOAD_STATUE_RELEASE ||
                mCurrentLoadingStatus == LOAD_STATUE_LOADING
                ) {
            finalMarginBottom = 0;
        }
        initAnimatorAndStart(currentMarginBottom, finalMarginBottom, false);
    }

    /**
     * 初始化动画参数和接口，并开始动画
     *
     * @param startValue
     * @param endValue
     * @param flag
     */
    protected void initAnimatorAndStart(float startValue, float endValue, final boolean flag) {
        animator = ValueAnimator.ofFloat(startValue, endValue);
        animator.setInterpolator(new DecelerateInterpolator(1));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                if (flag) {
                    setRefreshViewMarginTop((int) value);
                } else {
                    setLoadViewMarginBottom((int) value);
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (flag) {
                    if (mCurrentRefreshStatus == REFRESH_STATUE_RELEASE && refreshAndLoadingListener != null) {
                        mCurrentRefreshStatus = REFRESH_STATUE_REFRESHING;
                        refreshAndLoadingListener.onRefreshing();
                    } else if (mCurrentRefreshStatus == REFRESH_STATUE_OVER) {
                        mCurrentRefreshStatus = REFRESH_STATUE_NORMAL;
                    }
                    refreshStatueCallBack.callBack(mCurrentRefreshStatus, "");
                } else {
                    if (mCurrentLoadingStatus == LOAD_STATUE_RELEASE && refreshAndLoadingListener != null) {
                        mCurrentLoadingStatus = LOAD_STATUE_LOADING;
                        refreshAndLoadingListener.onLoading();
                    } else if (mCurrentLoadingStatus == LOAD_STATUE_OVER) {
                        mCurrentLoadingStatus = LOAD_STATUE_NORMAL;
                    }
                    loadStatueCallBack.callBack(mCurrentLoadingStatus, null);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.setDuration(200);
        animator.start();
        mCurrentDrag = false;
    }

    /**
     * 清除动画
     */
    protected void clearAnimator() {
        if (animator != null && animator.isStarted() && animator.isRunning()) {
            animator.removeAllListeners();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                animator.pause();
            }
            animator.cancel();
        }
    }

    /**
     * 根据偏移量来更新上拉的状态
     */
    private void updateLoadStatueByOffset(int offset) {
        if(mCurrentLoadingStatus == LOAD_STATUE_LOADING ||
        mCurrentLoadingStatus == LOAD_STATUE_OVER){
            return ;
        }
        if (offset >= 0) {
            mCurrentLoadingStatus = LOAD_STATUE_NORMAL;
        } else if (offset < 0 && Math.abs(offset) < mLoadViewHieght) {
            mCurrentLoadingStatus = LOAD_STATUE_PULL_UP;
        } else if (Math.abs(offset) >= mLoadViewHieght) {
            mCurrentLoadingStatus = LOAD_STATUE_RELEASE;
        }
        loadStatueCallBack.callBack(mCurrentLoadingStatus, null);
    }

    /**
     * 判断是否可以向上滚动
     * @return
     */
    public boolean canScrollUp() {
        if (Build.VERSION.SDK_INT < 14) {
            return canScrollVertically(1) || this.getScrollY() < 0;
        } else {
            return canScrollVertically(1);
        }
    }

    /**
     * @param str
     */
    public void loadOver(String str) {
        mCurrentLoadingStatus = LOAD_STATUE_OVER;
        loadStatueCallBack.callBack(mCurrentLoadingStatus, str);
        restoreLoadView();
    }


    public void setRefreshAndLoadingListener(RefreshAndLoadingListener refreshAndLoadingListener) {
        this.refreshAndLoadingListener = refreshAndLoadingListener;
    }

    /**
     * 测量loadView的高度
     */
    private void measureLoadViewHeight() {
        if (mLoadView != null && mLoadViewHieght <= 0) {
            mLoadViewHieght = mLoadView.getMeasuredHeight();
            if (mLoadViewHieght > 0) {
                //将加载view隐藏到屏幕下方
                setLoadViewMarginBottom(-mLoadViewHieght + 1);
            }
        }
    }

    public void setLoadStatueCallBack(BaseStatueCallBack callBack){
        this.loadStatueCallBack = callBack;
    }
    protected abstract void measureRefreshViewHeight();

    public abstract void refreshOver(String str);

    public abstract boolean canScrollDown();

    protected abstract void updateRefreshStatueByOffset(int offset);

    protected abstract void restoreRefreshView();

    protected abstract void setRefreshViewMarginTop(int marginTop);

    protected abstract void doRefreshJob(MotionEvent e);

    protected abstract void setRefreshCallBack(BaseStatueCallBack callBack);
}
