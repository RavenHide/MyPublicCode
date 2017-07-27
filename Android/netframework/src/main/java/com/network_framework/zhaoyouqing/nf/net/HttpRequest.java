package com.network_framework.zhaoyouqing.nf.net;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Zhaoyouqing on 2017/7/26.
 */

public abstract class HttpRequest<T> {
    private HttpCallback<T> callback;

    public void execute(final HttpCallback callback) {
        this.callback = callback;
        Observable<T> observable = onExecute();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    protected abstract Observable<T> onExecute();

    protected abstract void beforeSubsribe(Disposable d);

    protected abstract void onSession(Disposable d);

    private final Observer<T>  observer = new Observer<T>() {
        private Disposable disposable;
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            callback.onStart(d);
            disposable = d;
        }

        @Override
        public void onNext(@NonNull T data) {
            if(HttpRequest.this.callback != null){
                HttpRequest.this.callback.onSuccess(data);
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            e.printStackTrace();
            disposed(disposable);
            if(HttpRequest.this.callback != null){
                HttpRequest.this.callback.onError(e);
            }

        }

        @Override
        public void onComplete() {
            disposed(disposable);
        }
    };

    private void disposed(Disposable disposable){
        if(disposable != null && !disposable.isDisposed()){
            disposable.dispose();
        }
    }

}
