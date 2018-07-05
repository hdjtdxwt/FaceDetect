package com.epsit.ihealth.robot.requestbean;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2018/7/5/005.
 */

public class FaceImgLibInitResponse extends BaseResponse<List<FaceImgLibInitResponse.DataBean>>{

    public static class DataBean extends DataSupport {
        /**
         * id : 267ebbf2058f47ff9b591f6b3ce669aa
         * code : user1
         * name : 彭女士
         * idNo : 441201195612111201
         * type : 1
         * faceImg : http://192.168.1.9:8080/img/userfiles/1/images/photo/2017/12/user1.jpg
         * idImg :
         */

        protected String id;
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
        }
    }
}
