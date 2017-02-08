package com.hongqing.real_time_tracking;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by 贺红清 on 2017/2/7.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化操作
        SDKInitializer.initialize(getApplicationContext());
    }
}
