package com.epsit.ihealth.robot.base;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Administrator on 2018/7/4.
 */

public class RobotApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "44c0921e1c", false);
    }
}
