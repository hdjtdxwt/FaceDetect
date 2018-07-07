package com.epsit.ihealth.robot.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.epsit.facelibrary.FaceDetectHelper;
import com.epsit.ihealth.robot.R;
import com.epsit.ihealth.robot.base.RegisterImageCameraActivity;

import java.io.File;

/**
 * 人脸管理，直接操纵人脸库，从本地地址初始化人脸库什么的
 */
public class FaceManagerActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG = "FaceManagerActivity";
    String selfPath = Environment.getExternalStorageDirectory()+"/self77.jpg";
    String dir = Environment.getExternalStorageDirectory()+"/faceimg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_manager);
        FaceDetectHelper.initFaceTracker(90, getApplicationContext());
        findViewById(R.id.initSelf).setOnClickListener(this);
        findViewById(R.id.initAllDir).setOnClickListener(this);
        findViewById(R.id.deleteAll).setOnClickListener(this);
        findViewById(R.id.compare).setOnClickListener(this);
        findViewById(R.id.addFromCamera).setOnClickListener(this);
        Log.e(TAG,"人脸库中的人脸数量："+FaceDetectHelper.getAllCount());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.initAllDir:
                new Thread(){
                    @Override
                    public void run() {
                        initDir();
                    }
                }.start();

                Log.e(TAG,"所有人脸数："+FaceDetectHelper.getAllCount());
                break;
            case R.id.initSelf:
                initSingle(selfPath);
                Log.e(TAG,"所有人脸数："+FaceDetectHelper.getAllCount());
                break;
            case R.id.deleteAll:
                deleteAll();
                break;
            case R.id.compare:
                compare();
                break;
            case R.id.addFromCamera:
                //startActivity(new Intent(this, AddFaceFromCameraActivity.class));
                startActivity(new Intent(this, RegisterImageCameraActivity.class));
                break;

        }
    }
    public void compare(){
        String path1 = "mnt/sdcard/self77.jpg";
        String path2 = "mnt/sdcard/faceimg/075.jpg";
        initSingle(path1);
        initSingle(path2);
    }
    public void deleteAll(){
        int result = FaceDetectHelper.deleteAll();
        Log.e(TAG,"删除所有后返回："+result+"  result<0 表示失败；result=0表示成功");
    }
    public void initSingle(String selfPath){
        if(new File(selfPath).exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(selfPath);
            int faceId = FaceDetectHelper.addFace(bitmap,bitmap.getWidth(),bitmap.getHeight());
            Log.e(TAG,selfPath+ " 初始化结果 faceId="+faceId+"  初始化人脸结果："+(faceId>0 ? "成功":"失败"));

            /*if(FaceDetectHelper.isFaceAdded(bitmap,bitmap.getWidth(),bitmap.getHeight())){
                Log.e(TAG,selfPath+"  图片对应人脸添加添加过了");
            }else{
                int faceId = FaceDetectHelper.addFace(bitmap,bitmap.getWidth(),bitmap.getHeight());
                Log.e(TAG,selfPath+ " 初始化结果 faceId="+faceId+"  初始化人脸结果："+(faceId>0 ? "成功":"失败"));
            }*/
            bitmap.recycle();
        }else{
            Log.e(TAG,"人脸图片不存在");
        }
    }
    public void initDir(){
        File file = new File(dir);
        if(file.isDirectory()){
            File[]files = file.listFiles();
            for(File targetFile :files){
                if(targetFile.getName().endsWith("jpg") || targetFile.getName().endsWith("png") ){
                    initSingle(targetFile.getPath());
                }
            }
        }

    }
}
