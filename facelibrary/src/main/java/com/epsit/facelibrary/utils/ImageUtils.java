package com.epsit.facelibrary.utils;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;

import com.epsit.facelibrary.constant.SenseConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/7/4/004.
 */

public class ImageUtils {
    public static void saveImageData(byte[] imageData) {
        File imageFile = getOutputMediaImage();
        if (imageFile == null) return;
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(imageData);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }

        }
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }
    public static byte[] rotateYUV420Degree270(byte[] data, int imageWidth, int imageHeight){
        byte[] yuv =new byte[imageWidth*imageHeight*3/2];
        // Rotate the Y luma
        int i =0;
        for(int x = imageWidth-1;x >=0;x--){
            for(int y =0;y < imageHeight;y++){
                yuv[i]= data[y*imageWidth+x];
                i++;
            }
        }// Rotate the U and V color components
        i = imageWidth*imageHeight;
        for(int x = imageWidth-1;x >0;x=x-2){
            for(int y =0;y < imageHeight/2;y++){
                yuv[i]= data[(imageWidth*imageHeight)+(y*imageWidth)+(x-1)];
                i++;
                yuv[i]= data[(imageWidth*imageHeight)+(y*imageWidth)+x];
                i++;
            }
        }
        return yuv;
    }
    public static void saveYuv2Image(byte[] bytes, int width, int height) {
        FileOutputStream outStream = null;
        try {
            YuvImage yuvimage = new YuvImage(bytes, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);

            File imageFileDir = new File(SenseConfig.save_person_info);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File imageFile = new File(imageFileDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

            outStream = new FileOutputStream(imageFile);
            outStream.write(baos.toByteArray());
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public static void saveVideoData(byte[] imageData) {
        File imageFile = getOutputMediaVideo();
        if (imageFile == null) return;
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(imageData);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getOutputMediaImage() {
        File imageFileDir = new File(SenseConfig.save_person_info);
        if (!imageFileDir.exists()) {
            if (!imageFileDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File imageFile;
        imageFile = new File(imageFileDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return imageFile;
    }

    public static File getOutputMediaVideo() {
        File imageFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyTestImage");
        if (!imageFileDir.exists()) {
            if (!imageFileDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File imageFile;
        imageFile = new File(imageFileDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        return imageFile;
    }
}