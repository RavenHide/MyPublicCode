package com.example.zhaoyouqing.doublerecycleview.bean;

import java.io.Serializable;

/**
 * Created by Zhaoyouqing on 2017/7/14.
 */

public class SortBean implements Serializable{
    private static final long serialVersionUID = 3080246808350144200L;
    private String name;
    private String tag;
    private boolean isTitle;

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


    public SortBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
