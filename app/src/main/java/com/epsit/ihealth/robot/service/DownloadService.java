package com.epsit.ihealth.robot.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.aspsine.multithreaddownload.util.L;
import com.epsit.facelibrary.FaceDetectHelper;
import com.epsit.ihealth.robot.R;
import com.epsit.ihealth.robot.base.RobotApplication;
import com.epsit.ihealth.robot.dbentity.FaceImgDataBean;
import com.epsit.ihealth.robot.ebentity.EbDownloadFileInfo;
import com.epsit.ihealth.robot.entity.DownFileInfo;
import com.epsit.ihealth.robot.util.FileUtils;

import java.io.File;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

public class DownloadService extends Service {
    String TAG = "DownloadService";
    public static final int MSG_RESULT_SUCCESS = 10;//msg.what
    public static final int MSG_RESULT_FAIL = 11;//msg.what
    private boolean stop;//所有下载线程停止
    public static final String ACTION_DOWNLOAD = "com.exam.downloadservice";

    //核心线程3个，最大7个线程数  空闲的线程如果超过60s没执行，将被回收
    private static ThreadPoolExecutor sExecutorService;
    static{
        sExecutorService = new ThreadPoolExecutor(3, 7, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(128));
        sExecutorService.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());//拒绝并抛出异常
        //sExecutorService.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }
    //ThreadPoolExecutor pool = new ThreadPoolExecutor(3, 7, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(CAPACITY));
    Handler mHandler = new Handler(){ //主线程
        @Override
        public void handleMessage(Message msg) { //成功还是失败，发送到主界面的线程显示进度
            super.handleMessage(msg);
            switch (msg.what){ //下载成功还是失败
                case 0:
                    break;//线程池不够用，要先暂停下

                case MSG_RESULT_SUCCESS:
                    if(msg.obj!=null && msg.obj instanceof DownloadTask){
                        DownloadTask task = (DownloadTask)msg.obj;
                        EbDownloadFileInfo info  =new EbDownloadFileInfo(true,task.mDownloadUrl, task.mLocalSavePath+task.mLocalName);
                        EventBus.getDefault().post(info);
                    }

                    break;
                case MSG_RESULT_FAIL:
                    if(msg.obj!=null && msg.obj instanceof DownloadTask){
                        DownloadTask task = (DownloadTask)msg.obj;
                        EbDownloadFileInfo info  =new EbDownloadFileInfo(false,task.mDownloadUrl, task.mLocalSavePath+task.mLocalName);
                        EventBus.getDefault().post(info);
                    }
                    break;
            }
        }
    };
    private class DownloadTask implements Runnable {
        private String mLocalSavePath = FileUtils.faceimage;//默认保存路径
        private String mDownloadUrl;
        private String mLocalName;
        private String mFileSize;
        FaceImgDataBean dataBean;
        /**
         * -2等待-1失败,0执行前, 1执行中 ,2成功
         */
        // private int flag = -2;

        private long mTotalSize;
        private long mDownloadedSize;
        private long mDownloadedSpeed;
        private int mProgress;

        public DownloadTask(FaceImgDataBean bean ) {
            dataBean = bean;
            mDownloadUrl = bean.getFaceImg();
            if(TextUtils.isEmpty(mLocalName)){
                this.mLocalName = FileUtils.getFileName(mDownloadUrl);
            }
            this.mFileSize = FileUtils.getFormatSize(0);
        }

        protected void onPreExecute(String flag) {
            mTotalSize = 0;
            mDownloadedSize = 0;
            mDownloadedSpeed = 1;
            mProgress = 0;
            publishProgress(flag);
        }

        protected void onPostExecute(String flag) {
            publishProgress(flag);
        }

        private void publishProgress(String flag) {

        }

        public void execute() {
            onPreExecute(mDownloadUrl);
            boolean result = downloadFile();
            if (result) { //
                Message msg = mHandler.obtainMessage(MSG_RESULT_SUCCESS);
                msg.obj = this;
                msg.sendToTarget();
            } else {
                Message msg = mHandler.obtainMessage(MSG_RESULT_FAIL);
                msg.obj = this;
                msg.sendToTarget();
            }
            onPostExecute(mDownloadUrl);
        }

        private boolean downloadFile( ) {
            InputStream inputStream = null;
            RandomAccessFile raf = null;
            File file =null;//保存的文件
            try {
                // 检查文件夹是否存在，如果不存在就创建
                File dir = new File(mLocalSavePath);
                Log.e(TAG,"mLocalSavePath="+mLocalSavePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                    Log.e(TAG,"mLocalSavePath=创建父路径");
                }
                file = new File(mLocalSavePath, mLocalName);
                Log.e(TAG,file.toString()+"----保存路径");
                file.createNewFile();
                Log.e(TAG,"file。exist=? "+file.exists());
                raf = new RandomAccessFile(file, "rw");

                URL url = new URL(mDownloadUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url .openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                mTotalSize = urlConnection.getContentLength();
                byte[] buffer = new byte[1024];
                int bufferLength = 0;

                // kb
                long beginTime = System.currentTimeMillis();
                long endTime = beginTime;
                long size = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    if (stop) {
                        inputStream.close();
                        inputStream = null;
                        return false;
                    }
                    raf.write(buffer, 0, bufferLength);
                    mDownloadedSize += bufferLength;

                    size += bufferLength;
                    endTime = System.currentTimeMillis();
                    if (endTime - beginTime >= 1000) {
                        // kb
                        mDownloadedSpeed = size;
                        mProgress = (int) (100 * mDownloadedSize / mTotalSize);
                        publishProgress(mDownloadUrl);

                        beginTime = endTime;
                        size = 0;
                    }
                }
                raf.close();
                raf = null;
                inputStream.close();
                inputStream = null;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (raf != null) {
                        raf.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(file!=null){
                FaceDetectHelper.initFaceTracker(RobotApplication.getInstance().getApplicationContext());
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                int faceId = FaceDetectHelper.identify(bitmap,bitmap.getWidth(),bitmap.getHeight());
                if(faceId == -1) {
                    Log.e(TAG,"faceId = -1 图片没有检测到人脸");
                }else if(faceId==-111){ //不认识这个人
                    Log.e(TAG,"faceId = -111 不认识这个人");
                    int resultAddFace = FaceDetectHelper.addFace(bitmap,bitmap.getWidth(),bitmap.getHeight());
                    Log.e(TAG,"添加人脸返回的结果："+resultAddFace);
                    dataBean.setFaceId(resultAddFace);
                    RobotApplication.getInstance().getDaoSession().getFaceImgDataBeanDao().insert(dataBean);
                }else if(faceId>0){
                    Log.e(TAG,"已经有这个人了");
                }
            }

            return true;
        }

        @Override
        public void run() {
            execute();
        }
    }

    private void execute(Runnable runnable) {
        sExecutorService.execute(runnable);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stop = false;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop = true;
        mHandler.removeMessages(0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("DownloadService", "onStartCommand.intent=" + intent+"  thread="+Thread.currentThread().getName());
        if (intent != null) {
            FaceImgDataBean bean = intent.getParcelableExtra("bean");
            if(bean!=null && !TextUtils.isEmpty(bean.getFaceImg())){
                //这里要考虑超过线程池的问题
                DownloadTask task = new DownloadTask(bean);
                execute(task);
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}