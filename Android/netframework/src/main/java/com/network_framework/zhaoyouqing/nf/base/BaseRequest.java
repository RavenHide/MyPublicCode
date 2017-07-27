package com.network_framework.zhaoyouqing.nf.base;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.network_framework.zhaoyouqing.nf.ServierApi;
import com.network_framework.zhaoyouqing.nf.net.HttpClient;
import com.network_framework.zhaoyouqing.nf.net.HttpRequest;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Zhaoyouqing on 2017/7/26.
 */

public abstract class BaseRequest<T> extends HttpRequest<BaseResult<T>> {
    private Context mContext;
    private final static String BASE_URL = "https://www.bing.com/";
    private ServierApi servierApi;

    public BaseRequest(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    protected Observable<BaseResult<T>> onExecute() {
        //先初始化retrofit
        if (servierApi == null) {
            synchronized (BaseRequest.class) {
                if (servierApi == null) {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                            .serializeNulls().create();
                    Retrofit retrofit = new Retrofit.Builder()
                            .client(HttpClient.getInstance())
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .baseUrl(BASE_URL)
                            .build();
                    servierApi = retrofit.create(ServierApi.class);
                }
            }
        }
        return onExecute(servierApi);
    }

    /**
     * 执行相应的请求方法
     * @param servierApi
     * @return Observable<T>
     */
    protected abstract Observable<BaseResult<T>> onExecute(ServierApi servierApi);

    @Override
    protected void beforeSubsribe(Disposable d) {

    }
    @Override
    protected void onSession(Disposable d) {

    }

    public Context getmContext() {
        return mContext;
    }
}
