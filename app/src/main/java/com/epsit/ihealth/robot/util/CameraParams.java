package com.epsit.ihealth.robot.util;


import android.view.SurfaceView;

public class CameraParams {
    public SurfaceView surfaceView;
    public int preview_width;
    public int preview_height;
    public CameraHelper.PreviewFrameListener previewFrameListener;
    public int firstCameraId;
    public int camera_ori = -1;
    public int camera_ori_front = -1;
    public int pre_rate = 0;

    public CameraParams() {
    }
}
