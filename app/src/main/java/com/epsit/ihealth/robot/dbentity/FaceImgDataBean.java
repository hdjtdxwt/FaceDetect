package com.epsit.ihealth.robot.dbentity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2018/7/5.
 */

@Entity
public class FaceImgDataBean implements Parcelable{
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
    //这个属性后台没有返回，是自己保存的，先获取到图片后，放进阅面的人脸，阅面返回的人脸id，然后保存进来的
    protected int faceId ;

    @Generated(hash = 2015943533)
    public FaceImgDataBean(String id, String code, String name, String idNo, String type, String faceImg, String idImg, int faceId) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.idNo = idNo;
        this.type = type;
        this.faceImg = faceImg;
        this.idImg = idImg;
        this.faceId = faceId;
    }

    @Generated(hash = 1166145548)
    public FaceImgDataBean() {
    }

    protected FaceImgDataBean(Parcel in) {
        id = in.readString();
        code = in.readString();
        name = in.readString();
        idNo = in.readString();
        type = in.readString();
        faceImg = in.readString();
        idImg = in.readString();
        faceId = in.readInt();
    }

    public static final Creator<FaceImgDataBean> CREATOR = new Creator<FaceImgDataBean>() {
        @Override
        public FaceImgDataBean createFromParcel(Parcel in) {
            return new FaceImgDataBean(in);
        }

        @Override
        public FaceImgDataBean[] newArray(int size) {
            return new FaceImgDataBean[size];
        }
    };

    public int getFaceId() {
        return faceId;
    }

    public void setFaceId(int faceId) {
        this.faceId = faceId;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(code);
        dest.writeString(name);
        dest.writeString(idNo);
        dest.writeString(type);
        dest.writeString(faceImg);
        dest.writeString(idImg);
        dest.writeInt(faceId);
    }

}
