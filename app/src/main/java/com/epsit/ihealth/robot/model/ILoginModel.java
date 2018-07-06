package com.epsit.ihealth.robot.model;

/**
 * Created by Administrator on 2018/6/15/015.
 */

public interface ILoginModel {
    public interface OnLoginListener{
        void onSuccess();//
        void onFail();
    }
    interface getCountListener{
        //返回数据总数，需要下载的数量
        void getCount(int totalCount, int needDonwnload);
    }
    interface OnProgressListener{

    }
    //登录，登录的回调接口
    void login(String robotId, String password, ILoginModel.OnLoginListener listener);

    //通过机器人编号获取当前机构下 工作人员及取号人员人脸信息 (既有打卡人的人脸，也有取号人的人脸)
    void faceInfoByCustomize(getCountListener listener);

    //内部地图列表获取的方法
    /*Observable<MaplistResponse>getMaplist();
    void parseMaplistResponse(MaplistResponse data);

    //需要下载的地图的方法
    Observable<MapLoadResponse>getDownMapImg();
    void parseMapLoadResponse(MapLoadResponse data);*/
}
