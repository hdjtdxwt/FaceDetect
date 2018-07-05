package com.epsit.ihealth.robot.base;

import android.app.Application;

import com.epsit.ihealth.robot.retrofit.ApiManager;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Administrator on 2018/7/4.
 */

public class RobotApplication extends Application {
    static RobotApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CrashReport.initCrashReport(getApplicationContext(), "44c0921e1c", false);
    }
    public static RobotApplication getInstance(){
        return instance;
    }
}
