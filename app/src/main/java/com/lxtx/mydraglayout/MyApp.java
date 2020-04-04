package com.lxtx.mydraglayout;

import android.app.Application;
import android.content.Context;

/**
 * @author codejoyfun
 * @since 2020/3/22
 */
public class MyApp extends Application {
    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }
}
