package com.network_framework.zhaoyouqing.nf.listener;

/**
 * Created by Zhaoyouqing on 2017/7/27.
 * 用于在activity的被销毁时，解除观察者的绑定
 */

public interface DestroyListener {
    void onDestroy();
}
