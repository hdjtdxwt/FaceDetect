package com.epsit.ihealth.robot.dbentity;

/**
 * Created by Administrator on 2018/7/6/006.
 */

public class MapImgData {
    private String mapId; //图片id
    private String url;  // 下载地址
    private String version;  //版本
    private String delFag; //是否删除

    public String getMapId(){
        return mapId;
    }

    public void setMapId(String id){
        this.mapId=id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public String getDelFag() {
        return delFag;
    }

    public void setDelFag(String delFag) {
        this.delFag = delFag;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
