/*
 * Copyright (C) 2013 yixia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epsit.ihealth.robot.activity;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;

import com.epsit.facelibrary.CameraAction;
import com.epsit.facelibrary.callback.FaceDetectCallback;
import com.epsit.ihealth.robot.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoViewDemo extends Activity implements FaceDetectCallback {
    String TAG ="VideoViewDemo";
    SurfaceView surfaceView;
    VideoView mVideoView;
    CameraAction cameraAction;
    final String path = "http://gslb.miaopai.com/stream/3D~8BM-7CZqjZscVBEYr5g__.mp4";
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Vitamio.isInitialized(getApplicationContext());

        setContentView(R.layout.videoview);
        surfaceView = findViewById(R.id.surfaceView);
        playfunction();
        Log.e(TAG,"--------------->重新在新的界面添加人脸识别了");
        cameraAction= new CameraAction.Builder().init(VideoViewDemo.this).setSurfaceView(surfaceView).setCallback(true, CameraAction.TrackType.GREETING, VideoViewDemo.this).create();
        cameraAction.startTracker();
    }


    void playfunction() {

        mVideoView = (VideoView) findViewById(R.id.surface_view);
        play();

    }
    public void play(){
        mVideoView.setVideoPath(path);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.requestFocus();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //play();
            }
        });
    }

    @Override
    public void getFaceCount(int faceCount) {
        Log.e(TAG,"---Activity显示获取的人脸数："+faceCount);
        if(faceCount>0){
            Log.e(TAG,"---Activity显示获取的人脸数：count>0 有人脸");

            if(cameraAction!=null){
                cameraAction.removeCallback();
            }

            finish();
        }else{

        }
    }

    @Override
    public void nofindFaceHandler() {

    }
}
