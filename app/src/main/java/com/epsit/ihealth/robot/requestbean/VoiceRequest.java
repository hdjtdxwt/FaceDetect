package com.epsit.ihealth.robot.requestbean;

import com.google.gson.Gson;

/**
 * 向后台请求语句的应答结果的请求参数
 * Created by Administrator on 2017/5/27 0027.
 */

public class VoiceRequest {
    /**
     * accessToken : 123456
     * text : 在哪抽血
     * robotCode : 123456789
     * robotSn : 10
     * userId: '23423xx'
     */

    private String token;//token
    private String text;//要匹配的文本
    private String robotCode;//机器id
    private String robotSn;//流水号
    private String userId; //用户id

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
    public VoiceRequest(){

    }

    public VoiceRequest(String token, String text, String robotCode, String robotSn, String userId) {
        this.token = token;
        this.text = text;
        this.robotCode = robotCode;
        this.robotSn = robotSn;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public String getRobotSn() {
        return robotSn;
    }

    public void setRobotSn(String robotSn) {
        this.robotSn = robotSn;
    }

}
