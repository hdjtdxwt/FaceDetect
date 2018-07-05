package com.epsit.ihealth.robot.presenter;

import com.epsit.ihealth.robot.model.ILoginModel;

/**
 * Created by Administrator on 2018/6/14/014.
 */

public interface ILoginPresenter {
    void login(String robotId, String password, ILoginModel.OnLoginListener listener);

    //通过机器人编号获取当前机构下 工作人员及取号人员人脸信息 (既有打卡人的人脸，也有取号人的人脸)
    void faceInfoByCustomize();

    //考勤打卡  /api/customize/signin，原来是有身份证和人脸打卡的，现在只有打卡了
    void workerSign();

    //取号刷了人脸和身份证后，如果人证匹配后，需要保存用户的身份证和头像等信息  /api/customize/idInfoSave
    void saveUserInfo();

    //通过身份证来取号，原本这个接口也可以完成workerSign打卡的功能，后来去掉了，身份证不能打开
    void idCardTakeNumber();

    void showToast(String msg);
}
