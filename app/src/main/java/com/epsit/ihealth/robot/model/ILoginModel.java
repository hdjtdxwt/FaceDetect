package com.epsit.ihealth.robot.model;

/**
 * Created by Administrator on 2018/6/15/015.
 */

public interface ILoginModel {
    public interface OnLoginListener{
        void onSuccess();
        void onFail();
    }
    interface OnProgressListener{

    }
    //登录，登录的回调接口
    void login(String robotId, String password, ILoginModel.OnLoginListener listener);


    //内部地图列表获取的方法
    /*Observable<MaplistResponse>getMaplist();
    void parseMaplistResponse(MaplistResponse data);

    //需要下载的地图的方法
    Observable<MapLoadResponse>getDownMapImg();
    void parseMapLoadResponse(MapLoadResponse data);*/
}
