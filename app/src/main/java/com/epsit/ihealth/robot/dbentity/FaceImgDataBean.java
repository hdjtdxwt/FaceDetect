package com.epsit.ihealth.robot.dbentity;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2018/7/5.
 */

public class FaceImgDataBean extends DataSupport {
    /**
     * id : 267ebbf2058f47ff9b591f6b3ce669aa
     * code : user1
     * name : 彭女士
     * idNo : 441201195612111201
     * type : 1
     * faceImg : http://192.168.1.9:8080/img/userfiles/1/images/photo/2017/12/user1.jpg
     * idImg :
     */

    /*protected String id;
    protected String code;
    protected String name;
    protected String idNo;
    protected String type;
    protected String faceImg;
    protected String idImg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFaceImg() {
        return faceImg;
    }

    public void setFaceImg(String faceImg) {
        this.faceImg = faceImg;
    }

    public String getIdImg() {
        return idImg;
    }

    public void setIdImg(String idImg) {
        this.idImg = idImg;
    }*/

    private String name;
    private String code;
    private String job;
    private String type;
    private String greetings;
    private int priority;
    private String id;
    private List<HeadImageBean> headImage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGreetings() {
        return greetings;
    }

    public void setGreetings(String greetings) {
        this.greetings = greetings;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<HeadImageBean> getHeadImage() {
        return headImage;
    }

    public void setHeadImage(List<HeadImageBean> headImage) {
        this.headImage = headImage;
    }

    public static class HeadImageBean {
        /**
         * headImage : http://192.168.1.9:8080/img/userfiles/1/images/photo/2017/12/user4.jpg
         * version : 1.0
         */

        private String headImage;
        private String version;

        public String getHeadImage() {
            return headImage;
        }

        public void setHeadImage(String headImage) {
            this.headImage = headImage;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
