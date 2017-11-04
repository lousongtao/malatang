package com.shuishou.malatang;

import android.app.Application;

import com.shuishou.malatang.io.CrashHandler;

import java.io.File;

import pl.brightinventions.slf4android.FileLogHandlerConfiguration;
import pl.brightinventions.slf4android.LoggerConfiguration;

/**
 * Created by Administrator on 2017/10/18.
 */

public class MalatangApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileLogHandlerConfiguration fileHandler = LoggerConfiguration.fileLogHandler(this);
        File dir = new File(InstantValue.ERRORLOGPATH);
        if (!dir.exists())
            dir.mkdir();
        fileHandler.setFullFilePathPattern(InstantValue.ERRORLOGPATH + "/my_log.%g.%u.log");

        LoggerConfiguration.configuration().addHandlerToRootLogger(fileHandler);

        CrashHandler handler = CrashHandler.getInstance();
        handler.init(this);
    }
}
