package com.epsit.ihealth.robot.model.impl;

import android.media.AudioManager;


import com.epsit.ihealth.robot.model.IMainModel;

/**
 * Created by Administrator on 2018/7/2/002.
 */

public class IMainModelImpl implements IMainModel {
    String TAG = "IMainModelImpl";
    //声音最大值
    int maxVolume;
    //当前音量值
    int curVolume;
    //每一次声音调大调小的步调值
    int stepVolume;
    //声音管理器
    AudioManager audioMgr;
    boolean isPlaying;//当前是否在播放，默认false，不在播放

    @Override
    public int playTts(String tts) {
        return 0;
    }

    @Override
    public int playTts(String tts, String tag, int model) {
        return 0;
    }

    @Override
    public String getResult() {
        return null;
    }

    @Override
    public String getMarker() {
        return null;
    }

    @Override
    public int gotoMarker(String markerName) {
        return 0;
    }

    @Override
    public int moveCancel() {
        return 0;
    }

    @Override
    public int movePause(boolean flag) {
        return 0;
    }

    @Override
    public int voiceLarge() {
        return 0;
    }

    @Override
    public int voiceSmaller() {
        return 0;
    }

    @Override
    public int voiceMax() {
        return 0;
    }

    @Override
    public int voiceMin() {
        return 0;
    }

    @Override
    public int wakeUp(int index) {
        return 0;
    }

    @Override
    public String startID() {
        return null;
    }

    @Override
    public void stopID() {

    }

    @Override
    public String getUserID() {
        return null;
    }
    //引领带路任务回调

    //身份证识别相关介绍--end
}
