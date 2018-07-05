package com.epsit.ihealth.robot.requestbean;

import com.google.gson.Gson;

/**
 * 登陆传递的参数请求值
 * Created by Administrator on 2017/5/26 0026.
 */

public class LoginRequest {
    /**
     * code : ttyy_robot_01
     * secret : A567687DC876DFEA5467C876DFEA5467
     */
    public LoginRequest(){
    }
    public LoginRequest( String code, String secret) {
        this.robotId = code;
        this.secret = secret;
    }

    private String robotId;
    private String secret;

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
