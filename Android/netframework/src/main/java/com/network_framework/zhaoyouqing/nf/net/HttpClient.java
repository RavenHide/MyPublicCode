package com.network_framework.zhaoyouqing.nf.net;

import android.os.Build;
import android.support.compat.BuildConfig;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Zhaoyouqing on 2017/7/27.
 */

public class HttpClient {
    private static final String TAG = "HttpClient";

    private static OkHttpClient okHttpClient;
    private final static int DEFAULT_REQAD_TIMEOUT = 20001;
    private final static int DEFAULT_CONNECT_TIMEOUT = 20001;

    public static OkHttpClient getInstance() {
        if (okHttpClient == null) {
            synchronized (HttpClient.class) {
                if (okHttpClient == null) {
                    // File cacheFile = new File("缓存文件目录", "cache");
                    // Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //80Mb
                    okHttpClient = new OkHttpClient.Builder()
                            .readTimeout(DEFAULT_REQAD_TIMEOUT, TimeUnit.MILLISECONDS)
                            .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                            //.addNetworkInterceptor(new HttpCacheInterceptor())
                            .addInterceptor(new HttpLoggingInterceptor(logger))
                            //.cache(cache)
                            .build();
                }
            }
        }
        return okHttpClient;
    }

    private static class HttpCacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            //这里应添加检查网络连接状态的代码，这里无法获取application实例，暂定为true
//            if(true){
//                request = request.newBuilder()
//                        //CacheControl.FORCE_CACHE：仅仅使用缓存
//                        .cacheControl(CacheControl.FORCE_CACHE)
//                        .build();
//                //输出日志信息
//            }

            Response response = chain.proceed(request);
            return response;
        }
    }

    private static HttpLoggingInterceptor.Logger logger = new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
                try {
                    String content = URLDecoder.decode(message, "utf-8");
                    Log.d(TAG, "请求返回的内容: " + content);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.d(TAG, "UnsupportedEncodingException: " + message);
                }

        }
    };
}
