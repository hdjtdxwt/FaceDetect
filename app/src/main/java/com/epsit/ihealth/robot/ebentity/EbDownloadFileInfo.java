package com.epsit.ihealth.robot.ebentity;

/**
 * 下载文件成功还是失败通知主线程
 * Created by Administrator on 2018/7/5/005.
 */
public class EbDownloadFileInfo {
    private boolean isSuccess;//是否成功
    private String downloadUrl;//下载地址
    private String savePath;//保存路径

    public EbDownloadFileInfo(boolean isSuccess, String downloadUrl, String savePath) {
        this.isSuccess = isSuccess;
        this.downloadUrl = downloadUrl;
        this.savePath = savePath;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }
}
