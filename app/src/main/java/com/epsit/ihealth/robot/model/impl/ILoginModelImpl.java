package com.epsit.ihealth.robot.model.impl;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;


import com.epsit.ihealth.robot.base.RobotApplication;
import com.epsit.ihealth.robot.base.RobotLocalOperator;
import com.epsit.ihealth.robot.entity.CmsData;
import com.epsit.ihealth.robot.model.ILoginModel;
import com.epsit.ihealth.robot.requestbean.BaseRequest;
import com.epsit.ihealth.robot.requestbean.FaceImgLibInitResponse;
import com.epsit.ihealth.robot.requestbean.LoginRequest;
import com.epsit.ihealth.robot.requestbean.LoginResponse;
import com.epsit.ihealth.robot.retrofit.ApiManager;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import rx.android.schedulers.AndroidSchedulers;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.epsit.ihealth.robot.service.DownloadService.ACTION_DOWNLOAD;

/**
 * Created by Administrator on 2018/6/15/015.
 */

public class ILoginModelImpl implements ILoginModel {
    String TAG = "LoginModelImpl";
    OnLoginListener listener;
    String robotId;
    @Override
    public void login(String id, String password, OnLoginListener l) {
        robotId = id;
        this.listener = l;

        LoginRequest req = new LoginRequest(robotId, password);

        ApiManager.getInstance()
                .getApiService()
                .login(req).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                .subscribe(new Subscriber<LoginResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (listener != null) {
                            listener.onFail();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(LoginResponse data) {
                        if (data != null && "200".equals(data.getCode())) {
                            Log.e(TAG, data.toString());
                            if (data.getData() != null) {
                                //获取返回的系统的相关信息并保存
                                long systime = data.getData().getSystem_time();
                                String token = data.getData().getAccess_token();
                                long expertIn = data.getData().getExpires_in();
                                //setSysTime(systime);//设置系统时间

                                RobotApplication.getInstance().setRobotId(robotId);
                                RobotApplication.getInstance().setToken(token);

                                if(listener!=null){
                                    listener.onSuccess();
                                }
                            } else {
                                Log.e(TAG,"登陆获取的返回值userInfo的data为null");
                            }
                        }else{
                            Log.e(TAG,"data是空或者不是200返回");
                        }
                    }
                });

    }

    @Override
    public void faceInfoByCustomize(final getCountListener listener) {
        Log.e(TAG, "执行下图片的操作");
        String token = RobotApplication.getInstance().getToken();
        String robotId = RobotApplication.getInstance().getRobotId();
        BaseRequest request = new BaseRequest();
        request.setRobotId(robotId);
        request.setToken(token);
        ApiManager.getInstance()
                .getApiService()
                .faceInfoByCustomize(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FaceImgLibInitResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (listener != null) {
                            listener.getCount(0);
                        }
                    }

                    @Override
                    public void onNext(FaceImgLibInitResponse faceImgLibInitResponse) {
                        Log.e(TAG, "图片下载onNext->"+new Gson().toJson(faceImgLibInitResponse));
                        if (faceImgLibInitResponse != null && !TextUtils.isEmpty(faceImgLibInitResponse.getCode()) && "200".equals(faceImgLibInitResponse.getCode())) {
                            List<FaceImgLibInitResponse.DataBean> list = faceImgLibInitResponse.getData();
                            if (list != null && list.size() > 0) {
                                Log.e(TAG, "list有数据->"+list.size());

                                if (listener != null) {
                                    listener.getCount(list.size());
                                }

                                for(FaceImgLibInitResponse.DataBean bean: list ){
                                    Log.e(TAG,"bean: "+new Gson().toJson(bean));
                                    Log.e(TAG,"url="+bean.getFaceImg().trim());
                                    FaceImgLibInitResponse.DataBean dbBean = DataSupport.where("idImg = ?", bean.getFaceImg()).findFirst(FaceImgLibInitResponse.DataBean.class);
                                    if(dbBean==null){
                                        Log.e(TAG,"cursor没有这一条数据");
                                        Intent intent = new Intent(ACTION_DOWNLOAD);
                                        intent.putExtra("downloadUrl",bean.getFaceImg());
                                        bean.save();//保存到数据库，下载失败会删除
                                        RobotApplication.getInstance().startService(intent);
                                    }else{
                                        Log.e(TAG,"数据库有数据");
                                    }
                                }
                            } else {
                                if (listener != null) {
                                    listener.getCount(0);
                                }
                            }
                        } else {
                            if (listener != null) {
                                listener.getCount(0);
                            }
                        }
                    }
                });
    }


    /**
     * 手机需要root之后才可以（有时机器人时间忘记改回正确的，导致选套餐日期有问题，所以要保留根据后台时间设置日期功能）
     * 另外主要注意时区转换，就仅仅是在这个地方
     *
     * @param time
     */
    public void setSysTime(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd.HHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date date = new Date(time);
        try {
            //java.io.IOException: Cannot run program "su": error=13, Permission denied
            Process process = Runtime.getRuntime().exec("su");
            String datetime = dateFormat.format(date); //测试的设置的时间【时间格式 yyyyMMdd.HHmmss】
            Log.e(TAG, "datetime=" + datetime);
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT\n");
            os.writeBytes("/system/bin/date -s " + datetime + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
