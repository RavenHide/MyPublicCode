package com.example.zhaoyouqing.doublerecycleview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zhaoyouqing.doublerecycleview.interfaces.ViewCallBack;
import com.example.zhaoyouqing.doublerecycleview.presenter.BasePresenter;
import com.example.zhaoyouqing.doublerecycleview.widget.FlexibleLayout;

/**
 * Created by Zhaoyouqing on 2017/7/14.
 */

public abstract class BaseFragment<T extends BasePresenter, V> extends Fragment implements ViewCallBack<V>, View.OnClickListener{
    public T  presenter;
    protected Context mContext;
    protected boolean isPrepared;
    protected FlexibleLayout mFlexibleLayout;
    protected TextView tv_title;
    private LinearLayout mLinearLayout;
    protected TextView tv_Right;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = initView(inflater, container);
        initListener();
        mFlexibleLayout.loadData();
        isPrepared = true;
        return view;
    }

    public void showRightPage(int code){
        switch (code){
            case 0:
                mFlexibleLayout.showPageWithState(FlexibleLayout.State.Empty);
                break;
            case 1:
                mFlexibleLayout.showPageWithState(FlexibleLayout.State.Normal);
                break;
            case 2:
                mFlexibleLayout.showPageWithState(FlexibleLayout.State.NetWorkErro);
                break;

        }
    }

    protected void setTitle(String title){
        if(tv_title != null){
            tv_title.setText(title);
        }
    }

    protected void setBackColor(int color){
        if(mLinearLayout != null){
            mLinearLayout.setBackgroundColor(color);
        }
    }

    private ViewGroup initView(final LayoutInflater inflater, final ViewGroup container) {
        mFlexibleLayout = new FlexibleLayout(mContext) {
            @Override
            public ViewGroup initNormalView() {
                return initViewGrouup(inflater, container);
            }

            @Override
            public void onLoad() {
                if(presenter == null){
                    presenter = initPresenter();
                }else{
                    getData();
                }
            }
        };
        return mFlexibleLayout;
    }



    private ViewGroup initViewGrouup(LayoutInflater inflater, ViewGroup container) {
        ViewGroup view = (ViewGroup)inflater.inflate(getLayoutId(), container, false);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_Right = (TextView) view.findViewById(R.id.tv_right);
        if(tv_Right != null){
            tv_Right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRigthClick();
                }
            });
        }
        mLinearLayout = (LinearLayout) view.findViewById(R.id.top_layout);
        initCustomView(view);
        return view;
    }

    protected abstract void initCustomView(ViewGroup view);//初始化界面

    protected abstract T initPresenter();//初始化数据以及请求参数

    protected abstract void getData();

    protected abstract int getLayoutId();

    protected abstract void initListener();//初始化监听事件

    protected  void onRigthClick(){

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.add((ViewCallBack)this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.remove();
    }
}
