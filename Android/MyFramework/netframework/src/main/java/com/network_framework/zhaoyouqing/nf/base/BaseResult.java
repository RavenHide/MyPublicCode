package com.network_framework.zhaoyouqing.nf.base;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Zhaoyouqing on 2017/7/27.
 */

public class BaseResult<T> {
    @Expose
    private int state;
    @Expose
    private String msg;
    @Expose
    private T body;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    @Expose
    @SerializedName("images")
    private List<Image> images;
    @Expose
    @SerializedName("tooltips")
    private Tooltips tooltips;

    public Tooltips getTooltips() {
        return tooltips;
    }

    public void setTooltips(Tooltips tooltips) {
        this.tooltips = tooltips;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

   public class Image {
        @Expose
        @SerializedName("startdate")
        private String startdate;

        public String getStartdate() {
            return startdate;
        }

        public void setStartdate(String startdate) {
            this.startdate = startdate;
        }
    }
    public class Tooltips{
        @Expose
        @SerializedName("loading")
        private String loading;

        public String getLoading() {
            return loading;
        }

        public void setLoading(String loading) {
            this.loading = loading;
        }
    }
}
