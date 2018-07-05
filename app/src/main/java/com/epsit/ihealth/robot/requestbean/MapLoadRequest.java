package com.epsit.ihealth.robot.requestbean;

import com.epsit.ihealth.robot.dbentity.MapImgData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/5.
 */

public class MapLoadRequest extends BaseRequest {
    protected int pageNum;
    protected int pageSize;
    protected List<MapBean> ordImgs;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }


    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<MapBean> getOrdImgs() {
        return ordImgs;
    }

    public void setOrdImgs(List<MapBean> ordImgs) {
        this.ordImgs = ordImgs;
    }

    public void upData(List<MapImgData> list){
        if(ordImgs == null){
            ordImgs = new ArrayList<>();
        }
        if(list == null || list.size()<=0){
            return;
        }
        for(MapImgData map:list){
            MapLoadRequest.MapBean m=new MapLoadRequest.MapBean(map.getMapId(),map.getVersion());
            this.ordImgs.add(m);
        }
    }
    @Override
    public String toString() {
        return "MapLoadRequest{" +
                "\"accessToken\":\"" + token + '\"' +
                ", \"robotId\":\"" + robotId + '\"' +
                ", \"pageNum\":\"" + pageNum + '\"' +
                ", \"pageSize\":\"" + pageSize + '\"' +
                '}';
    }

    public static class MapBean {
        private String id;
        private String version;

        public MapBean(String id, String ver){
            this.id=id;
            this.version =ver;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"id\":'" + id + '\'' +
                    ", \"version\":" + version +
                    '}';
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
