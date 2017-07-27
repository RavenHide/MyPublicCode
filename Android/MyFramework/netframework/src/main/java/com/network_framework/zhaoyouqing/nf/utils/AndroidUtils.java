package com.network_framework.zhaoyouqing.nf.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Zhaoyouqing on 2017/7/27.
 */

public class AndroidUtils {
    /**
     * 检查网络连接情况
     * @param context
     * @return
     */
    public static final boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager _ConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (_ConnectivityManager != null) {
            final NetworkInfo[] _NetworkInfos = _ConnectivityManager.getAllNetworkInfo();
            if (_NetworkInfos != null) {
                for (int i = 0; i < _NetworkInfos.length; i++) {
                    if (_NetworkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
