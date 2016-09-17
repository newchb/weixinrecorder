package base;

import android.app.Application;

import manager.UiUtils;

/**
 * 创建者: hiboy
 * 创建时间: 2016/9/17.
 */
public class baseapplication extends Application{


    @Override
    public void onCreate() {
        super.onCreate();
        UiUtils.initview(this);
    }

}
