package com.epsit.ihealth.robot.retrofit;

import com.epsit.ihealth.robot.requestbean.BaseRequest;
import com.epsit.ihealth.robot.requestbean.FaceImgLibInitResponse;
import com.epsit.ihealth.robot.requestbean.LoginRequest;
import com.epsit.ihealth.robot.requestbean.LoginResponse;
import com.epsit.ihealth.robot.requestbean.MapLoadRequest;
import com.epsit.ihealth.robot.requestbean.MapLoadResponse;
import com.epsit.ihealth.robot.requestbean.VoiceRequest;
import com.epsit.ihealth.robot.requestbean.VoiceResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Administrator on 2018/7/5/005.
 */

public interface ApiService {
    @POST("http://trading.epsit.cn:8088/trading/app/textParse")
    Observable<VoiceResponse> getVoiceResponse(@Body VoiceRequest req);

    //登陆请求
    @POST("api/robot/token")
    Observable<LoginResponse> login(@Body LoginRequest requestBody);

    @POST("api/customize/faceInfoByCustomize")
    //@POST("api/face/getFaceInfo")
    Observable<FaceImgLibInitResponse> faceInfoByCustomize(@Body BaseRequest request);


    //地图图片请求
    @POST("api/img/download")
    Observable<MapLoadResponse> getMapLoadResponse(@Body MapLoadRequest req);

    //考勤打卡  /api/customize/signin，原来是有身份证和人脸打卡的，现在只有打卡了
    void workerSign();

    //取号刷了人脸和身份证后，如果人证匹配后，需要保存用户的身份证和头像等信息  /api/customize/idInfoSave
    void saveUserInfo();

    //通过身份证来取号，原本这个接口也可以完成workerSign打卡的功能，后来去掉了，身份证不能打开
    void idCardTakeNumber();
}
