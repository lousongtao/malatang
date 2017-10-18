package com.shuishou.malatang;

import android.app.Application;

import com.shuishou.malatang.io.CrashHandler;

/**
 * Created by Administrator on 2017/10/18.
 */

public class MalatangApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(this);
    }
}
