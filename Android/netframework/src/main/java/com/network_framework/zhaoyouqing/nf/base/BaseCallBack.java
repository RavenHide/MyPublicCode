package com.network_framework.zhaoyouqing.nf.base;

import android.content.Context;

import com.network_framework.zhaoyouqing.nf.listener.DestroyListener;
import com.network_framework.zhaoyouqing.nf.net.HttpCallback;
import com.network_framework.zhaoyouqing.nf.widget.ProgressDialog;

import io.reactivex.disposables.Disposable;

/**
 * Created by Zhaoyouqing on 2017/7/27.
 */

public class BaseCallBack<T> implements HttpCallback<BaseResult<T>>, DestroyListener{

    private final Context mContext;
    private final ProgressDialog mProgressDialog;
    private Disposable disposable;

    public BaseCallBack(Context mContext) {
        this(mContext, true);
    }

    BaseCallBack(Context mContext, final boolean showProgressDialog) {
        this.mContext = mContext;
        if (showProgressDialog) {
            this.mProgressDialog = new ProgressDialog.Builder(mContext).create();
        } else {
            mProgressDialog = null;
        }

    }

    @Override
    public void onStart(Disposable disposable) {
        if (this.mProgressDialog != null) {
            this.disposable = disposable;
            this.mProgressDialog.bindDisposable(this, disposable);
            this.mProgressDialog.show();
        }
    }

    @Override
    public void onError(Throwable e) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onCancel() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onSuccess(BaseResult<T> data) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        if (mProgressDialog != null && disposable != null) {
            mProgressDialog.dismiss();
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }
}
