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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myrecyclerview.commonAdapter.HeaderAndFooterWrapper;
import com.example.myrecyclerview.listener.RefreshAndLoadingListener;

/**
 * Created by Zhaoyouqing on 2017/7/20.
 */

public class RefreshRecyclerView extends RecyclerView {
    private static final String TAG = "MyRecyclerView";

    private View mRefreshView; // 下拉刷新的头部View
    private int mRefreshViewHeight; // 下拉刷新头部的高度
    private View mLoadView;//上拉加载的头部view
    private int mLoadViewHieght;//上拉加载头部的高度

    private int mFingerDownY;// 手指按下的Y位置
    private float mDragIndex = 0.35f;// 手指拖拽的阻力指数
    private boolean mCurrentDrag = false;

    private int mCurrentRefreshStatus = REFRESH_STATUE_NORMAL;   // 当前的状态
    private int mCurrentLoadingStatus = LOAD_STATUE_NORMAL;//当前加载状态

    // 默认状态
    protected static final int REFRESH_STATUE_NORMAL = 0x0011;
    protected static final int REFRESH_STATUE_PULL_DOWN = 0x0022;
    protected static final int REFRESH_STATUE_RELEASE = 0x0033;
    protected static final int REFRESH_STATUE_REFRESHING = 0x0044;
    protected static final int REFRESH_STATUE_OVER = 0x0055;

    protected static final int LOAD_STATUE_NORMAL = 0x001f;
    protected static final int LOAD_STATUE_PULL_UP = 0x002f;
    protected static final int LOAD_STATUE_RELEASE = 0x003f;
    protected static final int LOAD_STATUE_LOADING = 0x004f;
    protected static final int LOAD_STATUE_OVER = 0x005f;

    private ValueAnimator animator;
    private RefreshAndLoadingListener refreshAndLoadingListener;
    private ProgressBar progressBar;
    private TextView tv_desrcition;

    public RefreshRecyclerView(Context context) {
        this(context, null, 0);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        HeaderAndFooterWrapper headerAndFooterWrapper = (HeaderAndFooterWrapper) adapter;
        mRefreshView = headerAndFooterWrapper.getHeaderViews(0);
//        progressBar = mRefreshView.findViewById(R.id.pb_loading);
//        tv_desrcition = mRefreshView.findViewById(R.id.tv_descrition);
        mLoadView = headerAndFooterWrapper.getFooterViews(0);
        super.setAdapter(adapter);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            measureRefreshViewHeight();
            measureLoadViewHeight();

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
                if(!canScrollUp() && mCurrentRefreshStatus == REFRESH_STATUE_NORMAL){
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
                if(mCurrentDrag && mCurrentRefreshStatus == REFRESH_STATUE_NORMAL){
                    restoreLoadView();
                    break;
                }
                break;
        }
        return super.onTouchEvent(e);
    }

    /**
     * 做下拉刷新相关
     * @param e
     */
    private void doRefreshJob(MotionEvent e) {
        clearAnimator();
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

    /**
     * 做上拉加载相关
     * @param e
     */
    private void doLoadJob(MotionEvent e) {
        clearAnimator();
        if(mCurrentDrag){
            scrollToPosition(getAdapter().getItemCount() - 1);
        }
        int offsetY = (int) ((e.getRawY() - mFingerDownY) * mDragIndex);
        if(offsetY < 0){
            int marginBottom = -offsetY - mLoadViewHieght;
            updateLoadStatueByOffset(offsetY);
            setLoadViewMarginBottom(marginBottom);
            mCurrentDrag = true;
        }
    }


    /**
     * 通过改变refreshView的marginTop，来改变其位置
     * @param marginTop
     */
    private void setRefreshViewMarginTop(int marginTop) {
        if (marginTop < -mRefreshViewHeight + 1) {
            //限制marginTop的最小值为-mRefreshViewHeight + 1
            marginTop = -mRefreshViewHeight + 1;
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mRefreshView.getLayoutParams();
        params.topMargin = marginTop;
        mRefreshView.setLayoutParams(params);
    }

    /**
     * 通过改变loadView的marginBottom，来改变其位置
     *
     * @param marginBottom
     */
    private void setLoadViewMarginBottom(int marginBottom) {
//        if (marginBottom < -mLoadViewHieght + 1) {
//            marginBottom = -mLoadViewHieght + 1;
//        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mLoadView.getLayoutParams();
        params.bottomMargin = marginBottom;
        mLoadView.setLayoutParams(params);
    }

    private void restoreRefreshView() {
        int currentMarginTop = ((ViewGroup.MarginLayoutParams) mRefreshView.getLayoutParams()).topMargin;
        int finalMarginTop = -mRefreshViewHeight + 1;
        if (mCurrentRefreshStatus == REFRESH_STATUE_RELEASE
                || mCurrentRefreshStatus == REFRESH_STATUE_REFRESHING) {
            finalMarginTop = 0;
        }
        initAnimatorAndStart(currentMarginTop, finalMarginTop, true);
    }
    private void restoreLoadView(){
        int currentMarginBottom = ((ViewGroup.MarginLayoutParams)mLoadView.getLayoutParams()).bottomMargin;
        int finalMarginBottom = -mLoadViewHieght + 1;
        if(mCurrentLoadingStatus == LOAD_STATUE_RELEASE ||
                mCurrentLoadingStatus == LOAD_STATUE_LOADING){
            finalMarginBottom = 0;
        }
        initAnimatorAndStart(currentMarginBottom, finalMarginBottom, false);
    }

    /**
     * 初始化动画参数和接口，并开始动画
     *  @param startValue
     * @param endValue
     * @param flag
     */
    private void initAnimatorAndStart(float startValue, float endValue, final boolean flag) {
        animator = ValueAnimator.ofFloat(startValue, endValue);
        animator.setInterpolator(new DecelerateInterpolator(1));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                if(flag) {
                    setRefreshViewMarginTop((int) value);
                }else {
                    setLoadViewMarginBottom((int)value);
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(flag) {
                    if (mCurrentRefreshStatus == REFRESH_STATUE_RELEASE && refreshAndLoadingListener != null) {
                        mCurrentRefreshStatus = REFRESH_STATUE_REFRESHING;
                        updateRefreshStatueByOffset(0);
                        refreshAndLoadingListener.onRefreshing();
                    } else if (mCurrentRefreshStatus == REFRESH_STATUE_OVER) {
                        mCurrentRefreshStatus = REFRESH_STATUE_NORMAL;
                    }
                }else {
                    if(mCurrentLoadingStatus == LOAD_STATUE_RELEASE && refreshAndLoadingListener != null){
                        mCurrentLoadingStatus = LOAD_STATUE_LOADING;
                        updateLoadStatueByOffset(0);
                        refreshAndLoadingListener.onLoading();
                    }else if(mCurrentLoadingStatus == LOAD_STATUE_OVER){
                        mCurrentLoadingStatus = LOAD_STATUE_NORMAL;
                    }
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
    private void clearAnimator() {
        if (animator != null && animator.isStarted() && animator.isRunning()) {
            animator.removeAllListeners();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                animator.pause();
            }
            animator.cancel();
        }
    }

    /**
     * 根据偏移量来更新下拉的状态
     */
    private void updateRefreshStatueByOffset(int offset) {
        if (mCurrentRefreshStatus == REFRESH_STATUE_REFRESHING) {
            setWidgetStatue(View.VISIBLE, View.VISIBLE, "玩命加载中");
            return;
        }
        if (mCurrentRefreshStatus == REFRESH_STATUE_OVER) {
            setWidgetStatue(View.GONE, View.VISIBLE, "加载成功");
            return;
        }
        if (offset <= 0) {
            mCurrentRefreshStatus = REFRESH_STATUE_NORMAL;
            setWidgetStatue(View.GONE, View.VISIBLE, "");
        } else if (offset > 0 && offset < mRefreshViewHeight) {
            mCurrentRefreshStatus = REFRESH_STATUE_PULL_DOWN;
            setWidgetStatue(View.GONE, View.VISIBLE, "下拉刷新");
        } else if (offset >= mRefreshViewHeight) {
            mCurrentRefreshStatus = REFRESH_STATUE_RELEASE;
            setWidgetStatue(View.GONE, View.VISIBLE, "释放刷新");
        }
    }
    /**
     * 根据偏移量来更新上拉的状态
     */
    private void updateLoadStatueByOffset(int offset){
        if (mCurrentLoadingStatus == LOAD_STATUE_LOADING) {
//            setWidgetStatue(View.VISIBLE, View.VISIBLE, "玩命加载中");
            return;
        }
        if (mCurrentLoadingStatus == LOAD_STATUE_OVER) {
//            setWidgetStatue(View.GONE, View.VISIBLE, "加载成功");
            return;
        }
        if(offset >= 0){
            mCurrentLoadingStatus = LOAD_STATUE_NORMAL;
        }else if(offset < 0 && Math.abs(offset) < mLoadViewHieght){
            mCurrentLoadingStatus = LOAD_STATUE_PULL_UP;
        }else if(Math.abs(offset) >= mLoadViewHieght){
            mCurrentLoadingStatus = LOAD_STATUE_RELEASE;
        }
    }

    /**
     * 判断是否可以向下滚动
     *
     * @return
     */
    public boolean canScrollDown() {
        if (Build.VERSION.SDK_INT < 14) {
            return canScrollVertically(-1) || this.getScrollY() > 0;
        } else {
            return canScrollVertically(-1);
        }
    }

    /**
     * 判断是否可以向上滚动
     *
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
     * 刷新结束后调用
     */
    public void refreshOver() {
        mCurrentRefreshStatus = REFRESH_STATUE_OVER;
        updateRefreshStatueByOffset(0);
        restoreRefreshView();
    }
    public void loadOver(){
        mCurrentLoadingStatus = REFRESH_STATUE_OVER;
        updateLoadStatueByOffset(0);
        restoreLoadView();
    }


    private void setWidgetStatue(int progressBarStatue, int textViewStatue, String value) {
        progressBar.setVisibility(progressBarStatue);
        tv_desrcition.setVisibility(textViewStatue);
        tv_desrcition.setText(value);
    }

    public void setRefreshAndLoadingListener(RefreshAndLoadingListener refreshAndLoadingListener) {
        this.refreshAndLoadingListener = refreshAndLoadingListener;
    }

    /**
     * 测量loadView的高度
     */
    private void measureLoadViewHeight(){
        if (mLoadView != null && mLoadViewHieght <= 0) {
            mLoadViewHieght = mLoadView.getMeasuredHeight();
            if (mLoadViewHieght > 0) {
                //将加载view隐藏到屏幕下方
                setLoadViewMarginBottom(-mLoadViewHieght + 1);
            }
        }
    }
    private void measureRefreshViewHeight(){
        if (mRefreshView != null && mRefreshViewHeight <= 0) {
            mRefreshViewHeight = mRefreshView.getMeasuredHeight();
            if (mRefreshViewHeight > 0) {
                //将刷新view隐藏到屏幕上方
                setRefreshViewMarginTop(-mRefreshViewHeight + 1);
            }
        }
    }
}
