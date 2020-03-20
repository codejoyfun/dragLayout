package com.lxtx.mydraglayout;

import android.content.Context;

/**
 * @author 宁锟
 * @since 2020/3/20
 */
public class ScreenUtil {
    public static float dp(Context context, int dpValue) {
        return context.getResources().getDisplayMetrics().density * dpValue;
    }
}
