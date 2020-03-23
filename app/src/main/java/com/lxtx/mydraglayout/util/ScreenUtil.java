package com.lxtx.mydraglayout.util;

import com.lxtx.mydraglayout.MyApp;

/**
 * @author 宁锟
 * @since 2020/3/20
 */
public class ScreenUtil {
    public static float dp(int dpValue) {
        return MyApp.appContext.getResources().getDisplayMetrics().density * dpValue;
    }
}
