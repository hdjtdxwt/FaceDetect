package com.epsit.ihealth.robot.retrofit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import com.epsit.ihealth.robot.util.FileUtils;
import com.epsit.ihealth.robot.util.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/7/6.
 */

public class ImageDownLoader {
    private static final String ImageDownLoader_Log = Utils.makeLogTag(ImageDownLoader.class);
    public static final String mLocalSavePath = FileUtils.faceimage;
    /** 保存正在下载或等待下载的URL和相应失败下载次数（初始为0），防止滚动时多次下载 */
    private Hashtable<String, Integer> taskCollection;
    /** 缓存类 */
    private LruCache<String, Bitmap> lruCache;
    /** 线程池 */
    private ExecutorService threadPool;

    public ImageDownLoader(Context context) {
        // 获取系统分配给每个应用程序的最大内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        // 给LruCache分配最大内存的1/8
        lruCache = new LruCache<String, Bitmap>(maxMemory / 8) {
            // 必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
        taskCollection = new Hashtable<String, Integer>();
        // 创建线程数
        threadPool = Executors.newFixedThreadPool(10);
    }

    /**
     * 异步下载图片，并按指定宽度和高度压缩图片
     *
     * @param url
     * @param listener 图片下载完成后调用接口
     */
    public void loadImage(final String url, AsyncImageLoaderListener listener) {
        Log.i(ImageDownLoader_Log, "download:" + url);
        final ImageHandler handler = new ImageHandler(listener);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                downloadImage(url);
                //回调通知其他地方
            }
        };
        // 记录该url，防止滚动时多次下载，0代表该url下载失败次数
        taskCollection.put(url, 0);
        threadPool.execute(runnable);
    }

    /**
     * 下载图片，并按指定高度和宽度压缩
     * @return
     */
    private void downloadImage(String mDownloadUrl ) {
        InputStream inputStream = null;
        RandomAccessFile raf = null;
        try {
            // 检查文件夹是否存在，如果不存在就创建
            File dir = new File(mLocalSavePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String mLocalName = FileUtils.getFileName(mDownloadUrl);
            File file = new File(mLocalSavePath, mLocalName);
            if (file.exists()) {
                file.delete();
            }
            raf = new RandomAccessFile(file, "rw");

            URL url = new URL(mDownloadUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url .openConnection();
            urlConnection.setRequestMethod("GET");
            long mDownloadedSize =0;
            // urlConnection
            // .setRequestProperty("RANGE", "bytes=" + downloadedSize);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            long mTotalSize = urlConnection.getContentLength();
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            long size = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {

                raf.write(buffer, 0, bufferLength);
                mDownloadedSize += bufferLength;

                size += bufferLength;
            }
            raf.close();
            raf = null;
            inputStream.close();
            inputStream = null;
        } catch (Exception e) {
            e.printStackTrace();
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

    }

    /**
     * 取消正在下载的任务
     */
    public synchronized void cancelTasks() {
        if (threadPool != null) {
            threadPool.shutdownNow();
            threadPool = null;
        }
    }

    /**
     * 获取任务列表
     *
     * @return
     */
    public Hashtable<String, Integer> getTaskCollection() {
        return taskCollection;
    }

    /** 异步加载图片接口 */
    public interface AsyncImageLoaderListener {
        void onImageLoader(Bitmap bitmap);
    }

    /** 异步加载完成后，图片处理 */
    public  class ImageHandler extends Handler {

        private AsyncImageLoaderListener listener;

        public ImageHandler(AsyncImageLoaderListener listener) {
            this.listener = listener;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            listener.onImageLoader((Bitmap) msg.obj);
        }
    }
}