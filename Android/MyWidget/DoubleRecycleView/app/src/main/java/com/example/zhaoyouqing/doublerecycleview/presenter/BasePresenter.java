package com.example.zhaoyouqing.doublerecycleview.presenter;

import com.example.zhaoyouqing.doublerecycleview.interfaces.ViewCallBack;

/**
 * Created by Zhaoyouqing on 2017/7/14.
 */

public abstract class BasePresenter {
    protected ViewCallBack mViewCallBack;

    public void add(ViewCallBack mViewCallBack){
        this.mViewCallBack = mViewCallBack;
    }

    public void remove(){
        this.mViewCallBack = null;
    }

    protected  abstract void getData();
}
