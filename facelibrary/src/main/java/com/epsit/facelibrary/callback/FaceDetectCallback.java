package com.epsit.facelibrary.callback;

/**
 * Created by Administrator on 2018/7/3/003.
 */
import java.util.List;

public interface FaceDetectCallback {
    //public void findFaceHandler(List<FaceRect> featureList, int imageWidth, int imageHeight, List<String> nameList) ;
    //图片检测到的人脸数，faceCount大于0表示有人脸，小于等于0表示没有人脸，打招呼，闲时状态和工作状态
    void getFaceCount(int faceCount);

    void nofindFaceHandler();//没获取到人脸回到

    //考勤打卡，取号需要回调，返回faceId
    void getFaceId(int faceId);
}
