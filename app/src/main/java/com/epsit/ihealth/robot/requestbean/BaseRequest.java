package com.epsit.ihealth.robot.requestbean;

/**
 * 除了登录，后面的请求基本都带有token和robotId
 */
public class BaseRequest {
    protected String token;
    protected String robotId;

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
