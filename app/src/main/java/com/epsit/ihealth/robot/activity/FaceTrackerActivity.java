package com.epsit.ihealth.robot.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import com.epsit.facelibrary.CameraAction;
import com.epsit.facelibrary.callback.FaceDetectCallback;
import com.epsit.ihealth.robot.R;

/**
 * 人脸识别，当前是谁过来了
 */
public class FaceTrackerActivity extends AppCompatActivity implements FaceDetectCallback{

    String TAG = "CameraActivity";
    SurfaceView surfaceView;
    CameraAction cameraAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_tracker);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        cameraAction= new CameraAction.Builder().init(this).setSurfaceView(surfaceView)
                .setCallback(true, CameraAction.TrackType.SINGWORK, this).create();
        cameraAction.startTracker();
    }

    @Override
    public void getFaceCount(int faceCount) {

    }

    @Override
    public void nofindFaceHandler() {

    }

    //人脸识别用到的就是这个接口和上面的nofindFaceHandler
    @Override
    public void getFaceId(int faceId) {
        Log.e(TAG,"faceid="+faceId);
    }
}
