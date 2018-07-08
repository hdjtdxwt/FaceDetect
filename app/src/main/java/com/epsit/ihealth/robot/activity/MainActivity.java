package com.epsit.ihealth.robot.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.epsit.ihealth.robot.R;
import com.epsit.ihealth.robot.util.AlertError;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "=-----权限获取");
            requestAllPermissionsIfNeed();
        }
        File file = getCacheDir();
        if (file != null) {
            Log.e(TAG, file.toString());
        } else {
            Log.e(TAG, "获取的cacheDir是空");
        }

        findViewById(R.id.gologin).setOnClickListener(this);
        findViewById(R.id.gocamera).setOnClickListener(this);
        findViewById(R.id.gofacecheck).setOnClickListener(this);
        findViewById(R.id.gofacecheck2).setOnClickListener(this);
        findViewById(R.id.gofacecheck3).setOnClickListener(this);
        findViewById(R.id.gofacemanager).setOnClickListener(this);
    }


    @TargetApi(Build.VERSION_CODES.M)
    protected void requestAllPermissionsIfNeed() {
        List<String> permissionList = new ArrayList<String>();
        // 申请相机权限
        // Camera permission
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // 用户拒绝过权限申请，下一次再进入的时候给出的解释
                AlertError.showDialog(this, getResources().getString(R.string.error_title), getResources().getString(R.string.no_camera_perm_hint));
            } else {
                permissionList.add(Manifest.permission.CAMERA);
            }
        }
        // 我们需要从应用外的目录获取照片，所以需要申请读取外部存储权限
        // read external storage permission, for we need to read the photos
        // outside application-specific directories
        /*if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertError.showDialog(this, getResources().getString(R.string.error_title),
                        getResources().getString(R.string.no_file_perm_hint));
            } else {
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }*/
        if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.INTERNET)) {
                AlertError.showDialog(this, getResources().getString(R.string.error_title),
                        getResources().getString(R.string.no_file_perm_hint));
            } else {
                permissionList.add(Manifest.permission.INTERNET);
            }
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertError.showDialog(this, getResources().getString(R.string.error_title),
                        getResources().getString(R.string.no_file_perm_hint));
            } else {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
        if (permissionList.size() > 0) {
            requestPermissions(permissionList.toArray(new String[permissionList.size()]), 0);
        }
    }

    @Override
    public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                    (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                            ||
                            (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                            || (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this,"缺少权限！",Toast.LENGTH_SHORT).show();
                return;
            }else{ //有权限
                ///startActivity(new Intent(this, VideoViewDemo.class));
                //startActivity(new Intent(this, CameraActivity.class));
            }
        }else{
            //startActivity(new Intent(this, CameraActivity.class));
            //startActivity(new Intent(this, VideoViewDemo.class));
        }
        switch (v.getId()){
            case R.id.gologin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.gocamera:
                startActivity(new Intent(this, CameraActivity.class));
                break;
            case R.id.gofacecheck:
                //startActivity(new Intent(this, FaceRecoActivity.class));
                //startActivity(new Intent(this, FaceRecognitionActivity.class));
                startActivity(new Intent(this, FaceTrackerActivity.class));
                break;
            case R.id.gofacecheck2:
                startActivity(new Intent(this, FaceRecoActivity.class));
                //startActivity(new Intent(this, FaceRecognitionActivity.class));
                //startActivity(new Intent(this, FaceTrackerActivity.class));
                break;
            case R.id.gofacecheck3:
                //startActivity(new Intent(this, FaceRecoActivity.class));
                startActivity(new Intent(this, FaceRecognitionActivity.class));
                //startActivity(new Intent(this, FaceTrackerActivity.class));
                break;
            case R.id.gofacemanager:
                startActivity(new Intent(this, FaceManagerActivity.class));
                break;
        }


    }

}
