package com.epsit.facelibrary;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.epsit.facelibrary.callback.FaceDetectCallback;
import com.epsit.facelibrary.constant.SenseConfig;
import com.epsit.facelibrary.utils.ImageUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mobile.ReadFace.YMFace;

/**
 * Created by Administrator on 2018/7/3/003.
 */

public class CameraAction implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "FaceDetectAction";
    private SurfaceHolder mHolder;
    private CameraAction mFaceDetect;



    private long lastSearchTime = 0L;
    private Activity mContext;
    private Camera camera;
    private SurfaceView mSurfaceView;
    private HandlerThread mHandlerThread;
    //子线程中的handler
    private Handler mThreadHandler;
    private Handler mainHandler;
    private boolean isPlaying;//是否正在显示播放的内容
    private int iw, ih;//

    //针对人脸识别打招呼用的回调
    private FaceDetectCallback greetingFaceListener;
    private FaceDetectCallback freeFaceTrackListener;
    private FaceDetectCallback signFaceTrackListener;
    //是否要空闲识别人脸
    private boolean freeFaceTrackFlag = true;
    //当前是否在 空闲识别人脸
    private boolean freeFaceTrack_working = false;

    //是否要打招呼的相关操作
    private boolean greetingFlag = true;
    //当前是否在进行人脸的操作
    private boolean greeting_working = false;
    //是否要做上班打卡的操作
    private boolean signWorkFlag = false;
    //是否考勤识别人脸打卡上班
    private boolean signWork_working = false;
    //排队取号的操作
    private boolean takeNumberFlag = false;
    int cameraId;
    private Camera.Size size;
    public static int orientation = 0;//屏幕方向最终会导致

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Log.e(TAG,"surfaceCreated-->");
    }


    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        orientation = result;
        camera.setDisplayOrientation(result);
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        if(result==null){
            if(parameters.getSupportedPreviewSizes().size()>0){
                result = parameters.getSupportedPreviewSizes().get(0);
                result.width = 640;
                result.height = 480;
            }
        }
        return result;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //Log.e(TAG,"surfaceChanged-->");
        try {
            mHolder = holder;
            cameraId = getDefaultCameraId();
            //Log.e("surfaceChanged", "surfaceChanged-->cameraid=" + cameraId);
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e(TAG, "启动摄像头失败,可能是摄像头权限问题");
        }
        if (mHolder != null && camera != null) {
            try {
                camera.setPreviewDisplay(holder);
                //Log.e(TAG, "[设置Holder成功]");
            } catch (IOException var8) {
                var8.printStackTrace();
                //Log.e(TAG, "[设置Holder失败] ：" + var8.toString());
            }
        }

        if (camera != null) {
            Log.d("FaceDetectAction", "startDetect:开启摄像头成功");
            //Log.e(TAG, "[开启摄像头成功]");
            setCameraDisplayOrientation(mContext, cameraId, camera);
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = getBestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                //Log.e(TAG,"最佳尺寸：width="+size.width+"  height="+size.height);
                camera.setParameters(parameters);
            }else{
                parameters.setPreviewSize(640, 480);
                camera.setParameters(parameters);
            }
            camera.setPreviewCallbackWithBuffer(this);
            camera.addCallbackBuffer(new byte[size.width*size.height*ImageFormat.getBitsPerPixel(ImageFormat.NV21) /8 ]);
            FaceDetectHelper.initFaceTracker(orientation, mContext);
            //Log.e(TAG,"重新preview了");
            camera.startPreview();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        camera.addCallbackBuffer(data);
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        byte[] rotateData = ImageUtils.rotateYUV420Degree270(data, previewSize.width, previewSize.height);
        if (greetingFlag && !greeting_working ) { //需要人脸打招呼
            greeting_working = true; //当前正在操作，下次获取图像，不做这个处理
            Message msg = mThreadHandler.obtainMessage(SenseConfig.MSG_GREETING_FACETRACKING);
            msg.arg1 = previewSize.width;
            msg.arg2 = previewSize.height;
            msg.obj = data;
            msg.sendToTarget();

            //ImageUtils.saveYuv2Image(data, previewSize.width ,previewSize.height);//保存需要旋转，但是给检测的时候不需要

            //Log.e(TAG,"给子线程做打招呼处理");
        }
        if (signWorkFlag && !signWork_working) {//需要排队签到打卡
            signWork_working = true; //当前正在操作，下次获取图像，不做这个处理
            Message msg = mThreadHandler.obtainMessage(SenseConfig.MSG_SIGNING_FACETRACKING);
            msg.arg1 = previewSize.width;
            msg.arg2 = previewSize.height;
            msg.obj = data;
            msg.sendToTarget();
        }
        if (takeNumberFlag) {//取号识别人脸需要判断

        }
        if(freeFaceTrackFlag && !freeFaceTrack_working){
            freeFaceTrack_working = true;//当前在工作
            Message msg = mThreadHandler.obtainMessage(SenseConfig.MSG_FREE_FACETRACKING);
            msg.arg1 = previewSize.width;
            msg.arg2 = previewSize.height;
            msg.obj = data;
            msg.sendToTarget();
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //Log.e(TAG, "surfaceDestroyed-->release-->");
        release();
        mFaceDetect=null;
        if(mHandlerThread!=null){
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        if(mSurfaceView!=null){
            mSurfaceView=null;
        }
        if(mHolder!=null){
            mHolder=null;
        }
        if(camera!=null){
            camera.release();
            camera = null;
        }
    }

    private void initCamera() {
        if (camera != null) {
            camera.startPreview();
        }
    }

    public static enum TrackType {
        FREE_FACETRACKER,
        GREETING,
        SINGWORK,
        TAKENUMBER
    }

    private CameraAction() {
        mainHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                if(greetingFaceListener!=null){
                    //Log.e(TAG,"greetingFaceListener!=null");
                }
                switch (msg.what) {
                    case SenseConfig.MSG_GREETING_HASFACE://主动打招呼, 有识别到人脸，直接播放一句 你好
                        greeting_working = false;
                        if(greetingFaceListener!=null){
                            greetingFaceListener.getFaceCount(msg.arg1);//返回的是识别到了多少个人脸  ，如果需要做识别特定人脸打特定招呼，可以在这里修改
                        }
                        break;
                    case SenseConfig.MSG_GREETING_NOFACE: //主动打招呼没有识别到人脸
                        greeting_working = false;
                        if(greetingFaceListener!=null){
                            greetingFaceListener.getFaceCount(msg.arg1);
                        }
                        break;
                    case SenseConfig.MSG_FREE_FACETRACKING_HASFACE: //空闲状态人脸识别，发现人脸，需要从空闲状态变成工作状态
                        freeFaceTrack_working = false;
                        if(freeFaceTrackListener!=null){
                            freeFaceTrackListener.getFaceCount(msg.arg1);
                        }
                        break;
                    case SenseConfig.MSG_FREE_FACETRACKING_NOFACE: //空闲状态人脸识别，没有发现人脸
                        if(freeFaceTrackListener!=null){
                            freeFaceTrackListener.getFaceCount(msg.arg1);
                        }
                        freeFaceTrack_working = false;
                        break;
                    case SenseConfig.MSG_SIGNING_GETFACEID:
                        signWork_working = false;
                        if(signFaceTrackListener!=null){
                            signFaceTrackListener.getFaceId( msg.arg1);
                        }
                        break;
                    case SenseConfig.MSG_SIGNING_NOFACE:
                        signWork_working = false;
                        if(signFaceTrackListener!=null){
                            signFaceTrackListener.nofindFaceHandler();
                        }
                        break;

                }
            }
        };
        mHandlerThread = new HandlerThread("FaceDetect");
        mHandlerThread.start();
        mThreadHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                long time = System.currentTimeMillis();
                switch (msg.what){
                    case SenseConfig.MSG_GREETING_FACETRACKING: {
                        //Log.e(TAG, " 子线程收到消息，准备处理");
                        byte[] data = (byte[]) msg.obj;
                        if (data != null) {
                            //Log.e(TAG, "子线程的图片数据 不是null");
                            int iw = msg.arg1;
                            int ih = msg.arg2;
                            //Log.e(TAG, "iw=" + iw + "  ih=" + ih);
                            final List<YMFace> faces = FaceDetectHelper.trackMulti(data, iw, ih);
                            if (faces != null && faces.size() > 0) {
                                //Log.e(TAG, "faces != null && faces.size() > 0  有人脸");
                                Message message = mainHandler.obtainMessage(SenseConfig.MSG_GREETING_HASFACE);
                                message.arg1 = faces.size();
                                message.sendToTarget();
                            } else {
                                Message message = mainHandler.obtainMessage(SenseConfig.MSG_GREETING_NOFACE);
                                message.arg1 = 0;
                                message.sendToTarget();
                                //Log.e(TAG, "没有人脸！！");
                            }
                            long thisTime = System.currentTimeMillis();
                            //Log.e(TAG, "一张图片检测是否有人脸耗时：" + (thisTime - time));
                            time = thisTime;
                        } else {
                            //Log.e(TAG, "子线程的图片数据是null");
                            mainHandler.obtainMessage(SenseConfig.MSG_GREETING_NOFACE);
                        }

                    } break;
                    case SenseConfig.MSG_FREE_FACETRACKING: {
                        //Log.e(TAG, " 子线程收到消息，准备处理");
                        byte[] data = (byte[]) msg.obj;
                        if (data != null) {
                            //Log.e(TAG, "子线程的图片数据 不是null");
                            int iw = msg.arg1;
                            int ih = msg.arg2;
                            //Log.e(TAG, "iw=" + iw + "  ih=" + ih);
                            final List<YMFace> faces = FaceDetectHelper.trackMulti(data, iw, ih);
                            if (faces != null && faces.size() > 0) {
                                //Log.e(TAG, "faces != null && faces.size() > 0  有人脸");
                                Message message = mainHandler.obtainMessage(SenseConfig.MSG_FREE_FACETRACKING_HASFACE);
                                message.arg1 = faces.size();
                                message.sendToTarget();
                            } else {
                                Message message = mainHandler.obtainMessage(SenseConfig.MSG_FREE_FACETRACKING_NOFACE);
                                message.arg1 = 0;
                                message.sendToTarget();
                                //Log.e(TAG, "没有人脸！！");
                            }
                            long thisTime = System.currentTimeMillis();
                            //Log.e(TAG, "一张图片检测是否有人脸耗时：" + (thisTime - time));
                            time = thisTime;
                        } else {
                            //Log.e(TAG, "子线程的图片数据是null");
                            mainHandler.obtainMessage(SenseConfig.MSG_GREETING_NOFACE);
                        }
                    } break;
                    case SenseConfig.MSG_SIGNING_FACETRACKING:{ //考勤打开收到消息要人脸识别
                        //Log.e(TAG, " 子线程收到消息，准备处理");
                        byte[] data = (byte[]) msg.obj;
                        if (data != null) {
                            //Log.e(TAG, "子线程的图片数据 不是null");
                            int iw = msg.arg1;
                            int ih = msg.arg2;
                            //Log.e(TAG, "iw=" + iw + "  ih=" + ih);
                            final List<YMFace> faces = FaceDetectHelper.trackMulti(data, iw, ih);
                            if (faces != null && faces.size() > 0) {
                                int faceId = FaceDetectHelper.identify(data,iw,ih);

                                if(faceId>0){ //获取到了人脸id，可以通过人脸id去获取身份证等信息

                                    //Log.e(TAG, "考勤打开--->faces != null && faces.size() > 0  有人脸");
                                    Message message = mainHandler.obtainMessage(SenseConfig.MSG_SIGNING_GETFACEID);
                                    message.arg1 = faceId;
                                    message.sendToTarget();

                                }else{
                                    //Log.e(TAG, "考勤打开--->faces != null && faces.size() faceId不是大于0 没人脸或不识别");
                                    Message message = mainHandler.obtainMessage(SenseConfig.MSG_SIGNING_GETFACEID);
                                    message.arg1 = faceId;
                                    message.sendToTarget();
                                }

                            } else {
                                Message message = mainHandler.obtainMessage(SenseConfig.MSG_SIGNING_NOFACE);
                                message.arg1 = 0;
                                message.sendToTarget();
                                //Log.e(TAG, "没有人脸！！");
                            }
                            long thisTime = System.currentTimeMillis();
                            //Log.e(TAG, "一张图片检测是否有人脸耗时：" + (thisTime - time));
                            time = thisTime;
                        } else {
                            //Log.e(TAG, "子线程的图片数据是null");
                            mainHandler.obtainMessage(SenseConfig.MSG_GREETING_NOFACE);
                        }
                    }
                    break;
                }
            }
        };
    }

    private int getDefaultCameraId() {
        int defaultId = -1;

        // Find the total number of cameras available
        int mNumberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                defaultId = i;
            }
        }
        if (-1 == defaultId) {
            if (mNumberOfCameras > 0) {
                // 如果没有后向摄像头
                defaultId = 0;
            } else {
                // 没有摄像头
                Toast.makeText(mContext, "没有摄像头", Toast.LENGTH_LONG).show();
            }
        }
        return defaultId;
    }
    public static class Builder{
        Activity context;
        SurfaceView surfaceView;
        boolean greetingFlag;//工作状态人脸打招呼
        boolean signFaceTrackerFlag;//工作状态人脸打招呼
        boolean freeFaceTrackFlag;//空闲状态人脸识别
        private FaceDetectCallback greetingFaceListener;
        private FaceDetectCallback freeFaceTrackListener;
        private FaceDetectCallback signFaceTrackListener;
        public Builder(){

        }
        public Builder init(Activity context){
            this.context = context;
            return this;
        }
        public Builder setSurfaceView(SurfaceView surfaceView){
            this.surfaceView = surfaceView;
            return this;
        }
        public Builder setCallback(boolean flag, TrackType type, FaceDetectCallback faceDetectCallback) {
            //Log.e(TAG, "CameraAction setCallback");
            switch (type) {
                case GREETING:
                    //Log.e(TAG, "工作状态  人脸打招呼");
                    greetingFlag = flag;
                    greetingFaceListener = faceDetectCallback;
                    break;
                case SINGWORK:
                    signFaceTrackerFlag = flag;
                    signFaceTrackListener = faceDetectCallback;
                    break;
                case TAKENUMBER:

                    break;
                case FREE_FACETRACKER:
                    //Log.e(TAG, "空闲状态  人脸识别，有人就切换到工作状态");
                    freeFaceTrackFlag = flag;
                    freeFaceTrackListener = faceDetectCallback;
                    break;
            }
            return this;
        }
        public CameraAction create(){
            CameraAction cameraAction = new CameraAction();
            cameraAction.mContext = context;
            cameraAction.mSurfaceView = surfaceView;

            cameraAction.greetingFlag = greetingFlag;//工作状态人脸打招呼
            cameraAction.signWorkFlag = signFaceTrackerFlag;//是否要考勤打卡
            cameraAction.freeFaceTrackFlag = freeFaceTrackFlag;//空闲状态人脸识别
            cameraAction.greetingFaceListener = greetingFaceListener; //打招呼回调
            cameraAction.freeFaceTrackListener = freeFaceTrackListener;//空闲状态人脸识别回调
            cameraAction.signFaceTrackListener = signFaceTrackListener;//考勤打卡人脸识别回调
            cameraAction.mHolder = surfaceView.getHolder();
            return cameraAction;
        }
    }


    public void startTracker() {
        //Log.e(TAG, " startTracker() ");
        if (mHolder != null) {
            //Log.e(TAG, " mHolder != null ");
            mHolder.addCallback(this);
        }
    }


    public synchronized void removeCallback() {
        //Log.e(TAG, "[移除人脸识别回调] removeCallback()");
        release();
        greetingFaceListener = null;
        freeFaceTrackListener  = null;
        if(mHolder!=null){
            mHolder = null;
        }
    }

    public void release() {
        try {
            if (camera != null) {
                //Log.e(TAG, "[释放摄像头资源]");
                camera.stopPreview();
                camera.setPreviewCallback((Camera.PreviewCallback) null);
                camera.release();
                camera = null;

                //Log.e(TAG, "[释放摄像头资源成功]");

                FaceDetectHelper.release();
            }
        } catch (Exception var1) {

        }

    }

    public static void releaseFaceDetector() {

    }

    private static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
}
