package com.epsit.ihealth.robot.base;

/**
 * Created by Administrator on 2018/7/6/006.
 */
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.WindowManager;

import dou.utils.DLog;
import dou.utils.DisplayUtil;

/**
 * Created by mac on 2017/2/7 下午3:59.
 */

public abstract class FaceBaseActivity extends Activity {

    protected int sw;
    protected int sh;
    protected Context mContext;
    protected int orientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        sw = DisplayUtil.getScreenWidthPixels(this);
        sh = DisplayUtil.getScreenHeightPixels(this);
        DLog.d("onCreate : sw = " + sw + "  sh = " + sh);

    }


    public int getDoom(int tar) {
        if (RobotApplication.screenOri == Configuration.ORIENTATION_PORTRAIT) {
            return tar * sw / 1080;
        }
        return tar * sh / 1080;
    }

}


