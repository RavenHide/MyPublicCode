package com.network_framework.zhaoyouqing.nf.net;

import io.reactivex.disposables.Disposable;

/**
 * Created by Zhaoyouqing on 2017/7/26.
 * 结合rxjava的观察者来设计的回调接口
 */

public interface HttpCallback<T> {
    void onStart(final Disposable disposable);
    void onError(final Throwable e);
    void onCancel();
    void onSuccess(final T data);
}
