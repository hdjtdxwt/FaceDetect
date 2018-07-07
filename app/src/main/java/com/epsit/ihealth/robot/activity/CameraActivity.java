package com.epsit.ihealth.robot.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import com.epsit.facelibrary.CameraAction;
import com.epsit.facelibrary.callback.FaceDetectCallback;
import com.epsit.facelibrary.constant.SenseConfig;
import com.epsit.ihealth.robot.R;

public class CameraActivity extends AppCompatActivity implements FaceDetectCallback{
    String TAG = "CameraActivity";
    SurfaceView surfaceView;
    long lastHasFaceTime = 0l;
    CameraAction cameraAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraAction= new CameraAction.Builder().init(this).setSurfaceView(surfaceView).setCallback(true, CameraAction.TrackType.GREETING, this).create();
        cameraAction.startTracker();
    }

    @Override
    protected void onPause() {
        super.onPause();
        lastHasFaceTime=0L;
        if(cameraAction!=null)
            cameraAction.removeCallback();
    }

    @Override
    public void getFaceCount(int faceCount) {
        Log.e(TAG,"---Activity显示获取的人脸数："+faceCount);
        if(lastHasFaceTime==0L){
            lastHasFaceTime = System.currentTimeMillis();
        }else {
            if (faceCount > 0) {
                //Log.e(TAG, "---Activity显示获取的人脸数：count>0 有人脸");
                lastHasFaceTime = System.currentTimeMillis();
            } else {
                //没有人脸更新时间，如果连续30s没有人脸，就进入空闲状态，跳转界面，播放视频和音频
                if (System.currentTimeMillis() - lastHasFaceTime >= SenseConfig.FREE_TIME * 1000) {
                    if(cameraAction!=null){
                        cameraAction.removeCallback();
                    }
                    //startActivity(new Intent(this, EmptyActivity.class));
                    startActivity(new Intent(this, VideoViewDemo.class));
                }
                //Log.e(TAG, "---Activity显示获取的人脸数：count《=0 wu人脸");
            }
        }
    }

    @Override
    public void nofindFaceHandler() {

    }

    @Override
    public void getFaceId(int faceId) {

    }
}
