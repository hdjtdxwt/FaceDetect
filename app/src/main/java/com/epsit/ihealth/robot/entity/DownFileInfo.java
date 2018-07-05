package com.epsit.ihealth.robot.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/5/005.
 */

public class DownFileInfo implements Serializable{
    public static final int STATUS_NOT_DOWNLOAD = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_CONNECT_ERROR = 2;
    public static final int STATUS_DOWNLOADING = 3;
    public static final int STATUS_PAUSED = 4;
    public static final int STATUS_DOWNLOAD_ERROR = 5;
    public static final int STATUS_COMPLETE = 6;
    public static final int STATUS_INSTALLED = 7;

    private String saveFileName;//保存的名字
    private String url;//下载地址
    private int status;//当前状态

    public String getSaveFileName() {
        return saveFileName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSaveFileName(String saveFile) {
        this.saveFileName = saveFile;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
