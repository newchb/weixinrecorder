package manager;

import android.content.Context;

import base.baseapplication;

/**
 * 创建者: hiboy
 * 创建时间: 2016/9/17.
 */
public class UiUtils {

    private static Context context;

    public static void initview(baseapplication baseapplication) {
        context = baseapplication;
    }
    public static Context getcontext(){
        return context;
    }
}
