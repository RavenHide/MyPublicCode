package com.network_framework.zhaoyouqing.nf;

import com.network_framework.zhaoyouqing.nf.base.BaseResult;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by Zhaoyouqing on 2017/7/27.
 */

public interface ServierApi {
    @GET("HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN")
    Observable<BaseResult<String>> text();
}
