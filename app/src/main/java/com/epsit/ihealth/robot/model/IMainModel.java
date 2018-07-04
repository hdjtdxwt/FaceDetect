package com.epsit.ihealth.robot.model;

/**
 * Created by Administrator on 2018/7/2/002.
 */

public interface IMainModel {
    /**
     * 播放tts 动作 表情,云迹机器人没有动作和表情，只有语音文字播放，所以参数仅仅传语音文件就好了
     */
    int playTts(String tts);

    /**
     * 针对数字的读法，有二零一八和两千零一十八两种读法，可以自己设置
     * @param tts
     * @param tag 时间，小数，算术，金额
     * @param model 0是数字读法  1是文本读法
     * @return
     */
    int playTts(String tts, String tag, int model);

    /**
     * 语音识别文本回调,识别到用户说的一句话之后
     */
    String getResult();


    /**
     * 获取所有的点信息，在后台标记过的点,返回所有点的信息，包括标记点在的楼层，点的名称，坐标位置(xyz),点的属性（11或者0）
     * 所有的点信息组成的json数组对象
     * [{},{}]
     * @return
     */
    String getMarker();

    /**
     * 去特定的某一个点
     * @param markerName 点的名称，比如前台，  充电桩， 厕所
     * @return
     */
    int gotoMarker(String markerName);


    /**
     * 原来要去某一个点，可能迷路了，要取消移动的任务
     * RobotConnectAction.init(activity).setConnectCallback(robotConnectCallBack).sendCancel();
     * @return
     */
    int moveCancel();

    /**
     * 移动是否要暂停，通过机器人的软急停按钮暂停或关闭暂停（关闭暂停就相当于接着走），比如用户说了句，暂停一下，然后调用这个movePause(true);的方法让机器人停下来
     * @param flag
     * @return
     */
    int movePause(boolean flag);

    //声音调大
    int voiceLarge();

    //声音调小
    int voiceSmaller();

    //声音最大
    int voiceMax();

    //声音最小
    int voiceMin();

    //唤醒机器人开始识别声音，参数index表示拾音序号，0表示正前方
    int wakeUp(int index);

    //开启身份证识别
    String startID();
    //关闭身份证识别
    void stopID();
    // 获取身份证识别结果
    String getUserID();
}
