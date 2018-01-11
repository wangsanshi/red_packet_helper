package com.miu360.packet.base;

import android.app.Application;

/**
 * 作者：wanglei on 2018/1/9.
 * 邮箱：forwlwork@gmail.com
 */

public class App extends Application {
    public static App self;

    @Override
    public void onCreate() {
        super.onCreate();

        self = this;
    }
}
