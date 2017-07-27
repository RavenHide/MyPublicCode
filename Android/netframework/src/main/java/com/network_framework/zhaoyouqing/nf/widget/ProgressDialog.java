package com.network_framework.zhaoyouqing.nf.widget;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.network_framework.zhaoyouqing.netframework.R;
import com.network_framework.zhaoyouqing.nf.net.HttpCallback;

import io.reactivex.disposables.Disposable;

/**
 * Created by Zhaoyouqing on 2017/7/26.
 * 用于加载时显示的dialog
 */

public class ProgressDialog {
    private MyDialog mDialog;
    private HttpCallback<?> callback;
    private Disposable disposable;

    public ProgressDialog(final Context context, final boolean cancelable,
                          final boolean cancelOnTouchOutside) {
        this.mDialog = new MyDialog(context);
        this.mDialog.setCancelable(cancelable);
        this.mDialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
        this.mDialog.setOnCancelListener(onCancelListener);
    }

    //取消dialog的监听器
    private DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialogInterface) {
            if (callback != null) {
                callback.onCancel();
                disposable.dispose();
            }
            callback = null;
        }
    };

    /**
     * 把callback和disposable与当前实例的ProgressDialog的绑定在一起
     *
     * @param callback
     * @param disposable
     */
    public void bindDisposable(final HttpCallback<?> callback, final Disposable disposable) {
        this.callback = callback;
        this.disposable = disposable;
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();

    }

    /**
     * 链式建造者模式来创建builder
     */
    public static class Builder {
        private final Context mContext;
        private boolean mCancelOnTouchOutside = false;
        private boolean mCancelable = true;

        public Builder(final Context mContext) {
            this.mContext = mContext;
        }

        public Builder setCancelable(final boolean cancelable) {
            this.mCancelable = cancelable;
            return this;
        }

        public Builder setCancelOnTouchOutside(final boolean cancelOnTouchOutside) {
            this.mCancelOnTouchOutside = cancelOnTouchOutside;
            return this;
        }

        public ProgressDialog create() {
            final ProgressDialog progressDialog = new ProgressDialog(mContext,
                    mCancelable, mCancelOnTouchOutside);
            return progressDialog;
        }
    }

    private static class MyDialog extends Dialog {
        private float mScreenWidth;
        private float mItemWidth;

        public MyDialog(@NonNull Context context) {
            super(context, R.style.NF_DialogTheme);
            mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
            mItemWidth = context.getResources().getDimensionPixelOffset(R.dimen.progress_item);

            final View view = LayoutInflater.from(context).inflate(R.layout.nf_dialog_progress, null);
            final ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams((int) mScreenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.setContentView(view, layoutParams);

            final View view01 = findViewById(R.id.nf_progress_1);
            final View view02 = findViewById(R.id.nf_progress_2);
            final View view03 = findViewById(R.id.nf_progress_3);
            final View view04 = findViewById(R.id.nf_progress_4);
            final View view05 = findViewById(R.id.nf_progress_5);

            this.animator(view01, 0);
            this.animator(view02, 1);
            this.animator(view03, 2);
            this.animator(view04, 3);
            this.animator(view05, 4);

        }

        /**
         *
         * @param view 使用动画的view
         * @param index 根据index来设置延迟
         */
        private void animator(final View view, final int index) {
            final ObjectAnimator animator = ObjectAnimator.ofFloat(view, "x", -mItemWidth, mScreenWidth);
            animator.setDuration(2000l);
            animator.setInterpolator(new Interpolator());
            animator.setRepeatCount(-1);//表示一直重复
            animator.setStartDelay(index * 120);
            animator.start();
        }

    }

    /**
     * 插值器类，这里的定义是开始结束快，中间慢
     */
    private static class Interpolator implements TimeInterpolator{
        private static final float P0 = 0f;
        private static final float P1 = 0.6f;
        private static final float P2 = 0.5f;
        private static final float P3 = 0.5f;
        private static final float P4 = 1f;

        private static final float FIX = 0.7f;
        @Override
        public float getInterpolation(float v) {
            final float t = v / FIX;
            if(t > 1.0f){
                return 1.0f;
            }
            final float offset = 1f - t;
            final float _t = (float)(P0 * Math.pow(offset, 4) * Math.pow(t, 0) +
                    4 * P1 * Math.pow(offset, 3) * Math.pow(t, 1) +
                    4 * P2 * Math.pow(offset, 2) * Math.pow(t, 2) +
                    4 * P3 * Math.pow(offset, 1) * Math.pow(t, 3) +
                    P4 * Math.pow(offset, 0) * Math.pow(t, 4));
            return _t;
        }
    }

}
