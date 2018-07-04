package com.epsit.facelibrary;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.epsit.facelibrary.constant.SenseConfig;

import java.util.List;

import mobile.ReadFace.YMFace;
import mobile.ReadFace.YMFaceTrack;

/**
 * Created by Administrator on 2018/7/4/004.
 */

public class FaceDetectHelper {
    static String TAG ="FaceDetectHelper";
    public static interface GreetingFaceCallback{
        void getFaceCount(int count);
    }
    private static YMFaceTrack faceTrack;
    private static Context mContext;
    /**
     * 初始化阅面的人脸识别器
     */
    public static void initFaceTracker(int orientation,Context context) {
        if(faceTrack==null){
            faceTrack = new YMFaceTrack();
        }
        if(mContext==null && context!=null){
            mContext = context;
        }
        if(faceTrack!=null && mContext!=null){
            faceTrack.setDistanceType(YMFaceTrack.DISTANCE_TYPE_NEAR);//1-2米内的脸会识别

            faceTrack.setOrientation(orientation);
            //激活初始化，另一种是普通初始化
            int result = faceTrack.initTrack(mContext.getApplicationContext(), YMFaceTrack.FACE_270, YMFaceTrack.RESIZE_WIDTH_640, SenseConfig.appid, SenseConfig.appsecret);

            if (result == 0) {
                faceTrack.setRecognitionConfidence(75);
                Log.e(TAG, "初始化检测器成功");
                Toast.makeText(mContext, "初始化检测器成功", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "初始化检测器失败-->"+result);
                Toast.makeText(mContext, "初始化检测器失败！请打开wifi，同时检查camera权限是否有！", Toast.LENGTH_LONG).show();
            }
        }

    }
    public static void greetingCheck(byte[] bytes,int iw,int ih, GreetingFaceCallback callback) {
        final List<YMFace> faces = faceTrack.trackMulti(bytes, iw, ih);

        final byte[] data = bytes;
        if (faces != null && faces.size() > 0) {
            Log.e(TAG,"faces != null && faces.size() > 0");
            if(callback!=null){
                callback.getFaceCount(faces.size());
            }
        }else{
            if(callback!=null) {
                callback.getFaceCount(0);
            }
        }
    }
    public static List<YMFace> trackMulti(byte[]data, int iw, int ih){
        if(faceTrack!=null){
            return faceTrack.trackMulti(data,iw,ih);
        }
        return null;
    }

    public static void release(){
        if(faceTrack!=null){
            faceTrack.onRelease();

        }
        if(mContext!=null){
            mContext=null;
        }
    }

}
