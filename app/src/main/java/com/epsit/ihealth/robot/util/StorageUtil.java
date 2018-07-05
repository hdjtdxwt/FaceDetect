package com.epsit.ihealth.robot.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;
import java.io.IOException;

/**
 * 这个类的作用主要是用来获取磁盘空间大小，磁盘空间数值转换的作用
 * Created by Administrator on 2017/6/23 0023.
 */

public class StorageUtil {
    public static long getSdTotalSize(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return blockSize * totalBlocks;
        }
        return 0;
    }
    /**
     * 获得SD卡总大小
     *
     * @return
     */
    public static String getSDTotalSize(Context context) {
        return Formatter.formatFileSize(context, getSdTotalSize());
    }
    public static long getSdAvailableSize(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return blockSize*availableBlocks;
        }
        return 0;
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public static String getSDAvailableSize(Context context) {
        return Formatter.formatFileSize(context, getSdAvailableSize());
    }

    public static long getRomTotalSize(){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks;
    }
    /**
     * 获得机身内存总大小
     *
     * @return
     */
    public static String getRomTotalSize(Context context) {
        return Formatter.formatFileSize(context, getRomTotalSize());
    }

    public static long getRomAvailableSize(){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }
    /**
     * 获得机身可用内存
     *
     * @return
     */
    public static String getRomAvailableSize(Context context) {
        return Formatter.formatFileSize(context, getRomAvailableSize());
    }

    /**
     * 音频保存文件路径
     * @return
     */
    public static String getVideoFile(Context context){
        File file=null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            file = new File(Environment.getExternalStorageDirectory()+"/epsit/video/video.wav");
        }else{
            file = new File(context.getCacheDir()+"/epsit/video/video.wav");
        }
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file!=null?file.getAbsolutePath():"";
    }

}
