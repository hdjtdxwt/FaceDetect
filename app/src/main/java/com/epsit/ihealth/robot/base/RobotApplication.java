package com.epsit.ihealth.robot.base;

import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;

import com.tencent.bugly.crashreport.CrashReport;

import android.database.sqlite.SQLiteDatabase;

//下面两个导入根据配置的包名会变的，因为生成的类名也会跟配置包名变化
import com.epsit.ihealt.robot.greendao.gen.DaoMaster;
import com.epsit.ihealt.robot.greendao.gen.DaoSession;
/**
 * Created by Administrator on 2018/7/4.
 */

public class RobotApplication extends MultiDexApplication {
    private static RobotApplication instance;
    private String robotId;
    private String token;

    //绘制左右翻转
    public static final boolean yu = false;
    public static boolean reverse_180 = false;

    public static int  screenOri = Configuration.ORIENTATION_PORTRAIT;//竖屏

    private SQLiteDatabase db;
    private DaoMaster.DevOpenHelper mHelper;
    DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        setDatabase();
        CrashReport.initCrashReport(getApplicationContext(), "44c0921e1c", false);
    }

    public static RobotApplication getInstance() {
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

    private void setDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(this, "robotDb", null);
        db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
