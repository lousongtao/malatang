package com.shuishou.malatang;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/12/22.
 */

public final class InstantValue {
    public static final DateFormat DFYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String DOLLAR = "$";
    public static final String DOLLARSPACE = "$ ";
    public static final String NULLSTRING = "";
    public static final String RESULT_SUCCESS = "SUCCESS";
    public static final String RESULT_FAIL = "FAIL";
    public static final String PRICE = "$ ";
    public static final String CHECKDESK4MAKEORDER_AVAILABLE = "AVAILABLE";
    public static final String CHECKDESK4MAKEORDER_OCCUPIED = "OCCUPIED";

    public static final int DESKCELLWIDTH = 80;
    public static final int DESKCELLHEIGHT = 80;
    public static final int DESKCELL_AMOUNTPERROW = 13;
    public static final int DESKCELLA_MARGIN = 5;

    public static final String FORMAT_DOUBLE_2DECIMAL = "%.2f";

    public static String URL_TOMCAT = null;
    public static String BLUETOOTHUUID = null;
    public static String BLUETOOTHDEVICE = null;
    public static final String LOCAL_CATALOG_ERRORLOG = "/data/data/com.shuishou.malatang/errorlog/";
    public static final String FILE_CONNECTION = "/data/data/com.shuishou.malatang/connectionconfig";
    public static final String FILE_DISHNAME = "/data/data/com.shuishou.malatang/dishnameconfig";
    public static final String ERRORLOGPATH = "/data/data/com.shuishou.malatang/errorlog/";
    public static final String LOCAL_CATEGORY_UPGRADEAPK = "/data/user/0/com.shuishou.malatang/files/";
    public static final String SERVER_CATEGORY_UPGRADEAPK = "upgradeApk";

    public static final String CONFIGS_CONFIRMCODE = "CONFIRMCODE";
}
