package com.example.myrecyclerview.statueCallBack;

import android.support.v4.util.SparseArrayCompat;
import android.view.View;

/**
 * Created by Zhaoyouqing on 2017/7/24.
 * 用于上拉或下拉或者别的操作，状态更新时，UI随着变化
 */

public abstract class BaseStatueCallBack {
    public SparseArrayCompat<View> mViews;
    protected View mConvert;

    public BaseStatueCallBack(View mConvert) {
        this.mConvert = mConvert;
        mViews = new SparseArrayCompat<>();
    }
    protected View getView(int id){
        View view = mViews.get(id);
        if(view == null){
            view = mConvert.findViewById(id);
            mViews.put(id, view);
        }
        return view;
    }

    /**
     * 用于回调更新UI
     * @param Statue
     */
    public abstract void callBack(int Statue, String str);
}
