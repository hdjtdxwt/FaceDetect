package com.epsit.facelibrary.constant;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2018/7/3/003.
 */

public class SenseConfig {
    public final static String save_person_info = new File(Environment.getExternalStorageDirectory() , "face").toString();

    public final static String appid = "e3ea3489b9456b8e166332bfc56ae706";
    public final static String appsecret = "24ce66ca5e9a263812f5d4f3809c59eb76232ce7";

    //人脸识别消息（消息发给子线程）
    public final static int MSG_GREETING_FACETRACKING = -100;
    //打招呼有识别到人脸的msgId，消息会回到主线程
    public final static int MSG_GREETING_HASFACE = -101;
    //打招呼没有识别到人脸的msgId，消息会回到主线程
    public final static int MSG_GREETING_NOFACE = -102;


}
