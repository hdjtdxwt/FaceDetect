package com.epsit.ihealth.robot.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import com.epsit.ihealth.robot.R;
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
    private static ThreadPoolExecutor sExecutorService = new ThreadPoolExecutor(3, 7, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(128));
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
        /**
         * -2等待-1失败,0执行前, 1执行中 ,2成功
         */
        // private int flag = -2;

        private long mTotalSize;
        private long mDownloadedSize;
        private long mDownloadedSpeed;
        private int mProgress;

        public DownloadTask( String saveName, String downloadUrl, long size) {
            if(TextUtils.isEmpty(saveName)){
                this.mLocalName = FileUtils.getFileName(downloadUrl);
            }else{
                this.mLocalName = saveName;
            }
            this.mDownloadUrl = downloadUrl;
            this.mFileSize = FileUtils.getFormatSize(size);
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
            try {
                // 检查文件夹是否存在，如果不存在就创建
                File dir = new File(mLocalSavePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(mLocalSavePath, mLocalName);
                if (file.exists()) {
                    file.delete();
                }
                raf = new RandomAccessFile(file, "rw");

                URL url = new URL(mDownloadUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url .openConnection();
                urlConnection.setRequestMethod("GET");
                // urlConnection
                // .setRequestProperty("RANGE", "bytes=" + downloadedSize);
                urlConnection.setDoOutput(true);
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
            String saveName = intent.getStringExtra("saveName");
            String downloadUrl = intent.getStringExtra("downloadUrl");
            if(TextUtils.isEmpty(saveName) && !TextUtils.isEmpty(downloadUrl)){
                saveName = FileUtils.getFileName(downloadUrl);
            }
            long fileSize = intent.getLongExtra("fileSize", 0);
            boolean stop = intent.getBooleanExtra("stop", false);
            if (!TextUtils.isEmpty(downloadUrl)) {
                //这里要考虑超过线程池的问题
                DownloadTask task = new DownloadTask(saveName, downloadUrl, fileSize);
                execute(task);
                /*if (sExecutorService.getActiveCount() == 3) {
                    // 超出最大线程池数，提示稍后下载
                    Intent intent2 = new Intent(ACTION_DOWNLOAD);
                    intent2.putExtra("state", "");
                    intent2.putExtra("speed", "");
                    intent2.putExtra("progress", 0);
                    // -2等待-1失败,0执行前, 1执行中 ,2成功
                    intent2.putExtra("flag", -2);
                    mHandler.sendMessage(mHandler.obtainMessage(0, intent2));
                } else {
                    DownloadTask task = new DownloadTask(saveName, downloadUrl, fileSize);
                    execute(task);
                }*/
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}