package com.epsit.facelibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.epsit.facelibrary.constant.SenseConfig;

import java.util.List;

import dou.utils.BitmapUtil;
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

    public static void initFaceTracker(Context context){
        initFaceTracker(90, context);
    }
    /**
     * 初始化阅面的人脸识别器
     */
    public static void initFaceTracker(int orientation, Context context) {
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
    //检测是否有人脸，根据camera获取的图像数据
    public static List<YMFace> trackMulti(byte[]data, int iw, int ih){
        if(faceTrack!=null){
            return faceTrack.trackMulti(data,iw,ih);
        }
        return null;
    }

    /**
     * camera获取的图像里的人脸是否已经注册过了
     * @param data
     * @param iw
     * @param ih
     * @return 返回值 personId > 0 //已经认识，不能再添加，可以选择删除之前的重新添加。如果 personId < 0 //还不认识，可以添加   返回0表示没有人脸
     */
    public static int isFaceAdded(byte[]data, int iw, int ih){
        List<YMFace>list = faceTrack.trackMulti(data,iw,ih);
        if(list!=null && list.size()>=1){ //有人，按理只能有一个人
            int personId = faceTrack.identifyPerson(0);//identifyPerson的参数传0，识别人脸
            return personId;
        }else{
            return 0;//返回0表示没有人脸
        }
    }

    /**
     * 判断一个bitmap里是否有注册过的人脸
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static int isFaceAdded(Bitmap bitmap,int width,int height){
        List<YMFace> ymFaces = faceTrack.detectMultiBitmap(bitmap);
        int personId = faceTrack.identifyPerson(0);//identifyPerson的参数传0，识别人脸
        return personId;
    }

    /**
     * 人脸比对
     * @param faceIndex1  人脸1在人脸库中的index值
     * @param faceIndex2  人脸2在人脸库中的index值
     * @return 相似度，相似度越高，说明可能是同一个人的情况越大，0-100  大于75分看成同一个人
     */
    public static int compareFaceFeature(int faceIndex1,int faceIndex2){
        if(faceIndex1>0 && faceIndex2>0){
            float[] feature1 = faceTrack.getFaceFeature(faceIndex1);
            float[] feature2 = faceTrack.getFaceFeature(faceIndex2);
            /**
             * feature1 : 特征1
             * feature2 : 特征2
             * return : 返回⽐对结果（0-100）
             */
            int confidence = faceTrack.compareFaceFeature( feature1, feature2);
            return confidence;
        }
        return 0;
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
