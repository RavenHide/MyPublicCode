package com.example.zhaoyouqing.doublerecycleview.widget;

import android.content.Context;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.zhaoyouqing.doublerecycleview.BuildConfig;
import com.example.zhaoyouqing.doublerecycleview.R;

/**
 * Created by Zhaoyouqing on 2017/7/14.
 */

public abstract class FlexibleLayout extends LinearLayout {
    private View mLoadingView;
    private View mNetworkErrorView;
    private View mEmptyView;
    private ViewGroup mSuccessView;
    private ProgressBar mLoadingProgress;
    private TextView mLoadingText;
    private View title;
    public enum State{
        Normal, Empty, Loading, NetWorkErro
    }
    public FlexibleLayout(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setClipToPadding(true);
        setFitsSystemWindows(true);
        inflate(context, R.layout.layout_all, this);
        mSuccessView = initNormalView();
        title = mSuccessView.findViewWithTag("title");
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mSuccessView, layoutParams);
        if(BuildConfig.DEBUG){
            Log.d("count---", String.valueOf(mSuccessView.getChildCount()));
        }
    }

    public FlexibleLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, -1);
    }

    public FlexibleLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract ViewGroup initNormalView();
    public abstract void onLoad();
    public void loadData(){
        showPageWithState(State.Loading);
        onLoad();
    }

    public void showPageWithState(State state) {
        if(state != State.Normal && title != null){
            String tag = (String) getChildAt(0).getTag();
            if(!TextUtils.equals(tag, "title")){
                mSuccessView.removeView(title);
                addView(title, 0);
            }
        }
        switch (state){
            case Normal:
                mSuccessView.setVisibility(VISIBLE);
                if(BuildConfig.DEBUG){
                    Log.d("count--->", String.valueOf(mSuccessView.getChildCount()));
                }
                View childAt = mSuccessView.getChildAt(0);
                if(childAt != null){
                    String tag = (String) childAt.getTag();
                    if(!TextUtils.equals(tag, "title") && title != null){
                        removeView(title);
                        mSuccessView.addView(title, 0);
                    }
                }
                if (mLoadingView != null) {
                    mLoadingView.setVisibility(GONE);
                }

                if (mNetworkErrorView != null) {
                    mNetworkErrorView.setVisibility(GONE);
                }

                if (mNetworkErrorView != null) {
                    mNetworkErrorView.setVisibility(GONE);
                }
                invalidate();
                break;
            case Loading:
                mSuccessView.setVisibility(GONE);
                if(mEmptyView != null){
                    mEmptyView.setVisibility(GONE);
                }
                if (mNetworkErrorView != null) {
                    mNetworkErrorView.setVisibility(GONE);
                }
                if(mLoadingView == null){
                    ViewStub viewStub = (ViewStub) findViewById(R.id.vs_loading);
                    mLoadingView = viewStub.inflate();
                    mLoadingProgress = (ProgressBar) mLoadingView.findViewById(R.id.loading_progress);
                    mLoadingText = (TextView) mLoadingView.findViewById(R.id.loading_text);
                }else {
                    mLoadingView.setVisibility(VISIBLE);
                }
                mLoadingProgress.setVisibility(VISIBLE);
                mLoadingText.setText("正在加载");
                break;
            case Empty:
                mSuccessView.setVisibility(GONE);
                if(mLoadingView != null){
                    mLoadingView.setVisibility(GONE);
                }
                if (mNetworkErrorView != null) {
                    mNetworkErrorView.setVisibility(GONE);
                }
                if(mEmptyView == null){
                    ViewStub viewStub = (ViewStub) findViewById(R.id.vs_end);
                    mEmptyView = viewStub.inflate();
                }else{
                    mEmptyView.setVisibility(VISIBLE);
                }
                break;
            case NetWorkErro:
                if (mLoadingView != null) {
                    mLoadingView.setVisibility(GONE);
                }

                if (mEmptyView != null) {
                    mEmptyView.setVisibility(GONE);
                }
                if(mNetworkErrorView == null){
                    ViewStub viewStub = (ViewStub) findViewById(R.id.vs_error);
                    mNetworkErrorView = viewStub.inflate();
                    View btnRetry = mNetworkErrorView.findViewById(R.id.btn_retry);
                    btnRetry.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onLoad();
                        }
                    });
                }else{
                    mNetworkErrorView.setVisibility(VISIBLE);
                }
                break;
            default:
                break;
        }
    }

}
