package com.epsit.ihealth.robot.base;

import android.app.Application;

import com.epsit.ihealth.robot.retrofit.ApiManager;
import com.tencent.bugly.crashreport.CrashReport;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

/**
 * Created by Administrator on 2018/7/4.
 */

public class RobotApplication extends LitePalApplication {
    private static RobotApplication instance;
    private String robotId;
    private String token;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //初始化数据库
        LitePal.initialize(this);

        CrashReport.initCrashReport(getApplicationContext(), "44c0921e1c", false);
    }
    public static RobotApplication getInstance(){
        return instance;
    }

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
