package com.epsit.ihealth.robot.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

/** 文件操作的类，主要包含了一个文件复制的方法
 * Created by Administrator on 2017/6/3 0003.
 */

public class FileUtils {
    static String TAG = "FileUtils";
//    public static String logFileName = "2017-8-24.txt"; // 文件名称 web
//    public static String logFileName1 = "2017-8-24.txt"; //文件名称 log
    public static String errorString = "uncaughtException.log";

    public static final String faceimage = "/epsit/faceimage/";
    public static final String path = "/epsit/mapimg/";
    public static final String taskPath="/epsit/task/";
    public static final String logPath="/epsit/log/";

    public static final int BYTE = 1024;

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / BYTE;
        if (kiloByte < 1) {
            return size + "B";
        }

        double megaByte = kiloByte / BYTE;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / BYTE;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / BYTE;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    /**
     * 将 sourcePath 文件复制备份成 destPath （注意，这么两个path都是文件的全路径，是文件而不是文件夹，这里没考虑复制文件夹的情况）
     * @param srcFileName 原始文件的全局路径
     * @param destFileName 目标文件的全路径
     */
    public static void copy(String srcFileName, String destFileName){
        File srcFile = new File(srcFileName);
        Log.e(TAG,"-----------copy");
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            Log.e(TAG,"源文件：" + srcFileName + "不存在！");
        } else if (!srcFile.isFile()) {
            Log.e(TAG,"制文件失败，源文件：" + srcFileName + "不是一个文件！");
        }
        // 判断目标文件是否存在
        File destFile = new File(destFileName);
        if (destFile.exists()) {
            // 删除已经存在的目标文件，无论目标文件是目录还是单个文件
            new File(destFileName).delete();
        } else {
            // 如果目标文件所在目录不存在，则创建目录
            if (!destFile.getParentFile().exists()) {
                // 目标文件所在目录不存在
                if (!destFile.getParentFile().mkdirs()) {
                    // 复制文件失败：创建目标文件所在目录失败

                }
            }
        }

        // 复制文件
        int byteread = 0; // 读取的字节数
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024*1024];
            Log.e(TAG,"save-----通过流在保存文件");
            while ((byteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteread);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getMapImgPath(){
        String parent = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (StorageUtil.getSdAvailableSize() >= 1024 * 1024 * 300) { //大于300m可用内存
                parent = Environment.getExternalStorageState() + path;
                File parentFile = new File(parent);
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
            } else if (StorageUtil.getRomAvailableSize() >= 1024 * 1024 * 300) { //rom内存可用
                parent = Environment.getDataDirectory() + path;
                File parentFile = new File(parent);
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
            }
        }
        return parent;
    }

    public static void write(String filename, InputStream in){
        File file=new File(getMapImgFile());
        Log.e(TAG, file.getAbsolutePath()+"======================");
        if(!file.exists()){
            if(!file.mkdirs()){//若创建文件夹不成功
                Log.e(TAG, "Unable to create external cache directory");
            }
        }
        File file1 = new File(file,filename);
        if(!file1.exists()){
            try {
                file1.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        OutputStream os=null;
        try{
            os=new FileOutputStream(file1);
            int ch=0;
            while((ch=in.read())!=-1){
                os.write(ch);
            }
            os.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                os.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        Log.e(TAG, "---=0-=写入成功");
    }



    /**
     * 获取地图文件的目录
     * @return
     */
    public static String getMapImgFile(){
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath()+path).getAbsolutePath();
    }

    public static String getBroTaskFile(){
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath()+taskPath).getAbsolutePath();
    }
    /**
     * 根据url获取图片名称和后缀
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    /**
     * 获取url 图片的后缀
     * @param url
     * @return
     */
    public static String getUrlSuffix(String url){
        if(url == null){
            Log.e(TAG, "url 为空");
            return ".png";
        }
        String str = getFileName(url);
        return str.substring(str.length()-4,str.length());
    }

    /**
     * bitmap保存到磁盘文件
     * @param bitmap
     * @param saveName
     */
    public static void bitmapSaveFile(Bitmap bitmap, String saveName){
        Log.e(TAG, "保存图片=====   " +saveName);
        File ff = new File(getMapImgFile());

        if (!ff.exists()) {
            ff.mkdirs();
        }

        File f = new File(ff,saveName);

        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getVoiceParent(Context context){
        String sourcePath = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            sourcePath = Environment.getExternalStorageDirectory().toString();
        }else{
            sourcePath = context.getExternalCacheDir().toString();
        }
        return sourcePath;

    }


    /**
     * 写出文件,采用当前项目编码
     *
     * @param filePath
     *            写入文件的目录，不存在则创建
     * @param append
     *            为true表示追加，false表示重头开始写，
     * @param text
     *            要写入的文本字符串
     * @throws IOException
     */
    public static void writeTxtFile(File filePath, boolean append, String text)
            throws IOException {
        long maxSize=524288;//1M;
        if((filePath.length()) > maxSize){
            append=false;
            //清空
            Log.e(TAG, filePath.length()+"=超过了=="+(filePath.length() / 1048576));
        }

        if (text == null) {
            return;
        }

        File pFile = filePath.getParentFile();
        if (!pFile.exists()) {
            pFile.mkdirs();
        }

        FileWriter fw = new FileWriter(filePath, append);
        BufferedWriter out = new BufferedWriter(fw);
        try {
            out.write(text);
        } finally {
            if (out != null)
                out.close();
            if (fw != null)
                fw.close();
        }
    }

    /**
     * 读取log
     *
     * @param context
     * @param type
     * @return
     */
    public static String readTxtFile(Context context, String type) {
        FileInputStream inStream;
        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+logPath+type);
            Log.e("FileUtils","日志路径 "+file.getAbsolutePath());
            inStream = new FileInputStream(file);
            byte[] data = readData(inStream);
            return new String(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "===";
    }
    private static byte[] readData(FileInputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

}
