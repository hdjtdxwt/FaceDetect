package com.epsit.ihealth.robot.base;

/**
 * Created by Administrator on 2018/7/5/005.
 */

public class AppConstant {

    public static final String SPFILENAME = "robot_info";
    /**
     * 语音对话聊天时，是否要保存用户说的语音文件
     */
    public static final String SP_VOICEFILE_SAVE = "voicefile_save";
    /**
     * 登陆的类型参数，login方法的第一个参数
     */
    public static final String loginType = "client_credential";
    /**
     * sharedpreferences文件保存robot信息的文件的名称
     */
    public static final String SP_ROBOTINFO_FNAME = "robot";
    /**
     * sharedpreferences文件保存robot信息的文件的名称
     */
    public static final String SP_HASEXIT = "has_exit";
    /**
     * 存到sharedpreferences里的机器人的唯一id,相当于登陆的用户名
     */
    public static final String SP_ROBOTID = "robot_id";
    /**
     * 存到sharedpreferences里的访问后台的token值
     */
    public static final String SP_ACCESS_TOKEN = "access_token";

    /**
     * 存到sharedpreferences里的旧用户的userId
     */
    public static final String SP_OLDUSERID = "olduserId";

    public static final String SP_NEWUSERID="newuserId";

    public static final String SP_NEWUSER_PHOTO="newuser_photo"; //新用户的头像地址
    /**
     * sharedpreferences里的token有效时长（单位是秒）
     */
    public static final String SP_TOKEN_EXPIRES = "token_expires_in";
    /**
     * 存到sharedpreferences里的后台的系统时间值(后台系统时间的毫秒值)
     */
    public static final String SP_SERVER_SYSTIME = "server_systime";

    /**
     * 存到sharedpreferences里的机器人的登陆密码
     */
    public static final String SP_ROBOT_SECRET= "robot_secret";

    /**
     * 存到sharedpreferences里的主页面是否显示人脸图像
     */
    public static final String SP_MAIN_SHOWFACES= "robot_main_showfaces";

    /**
     * 存到sharedpreferences里的聊天页是否显示人脸图像
     */
    public static final String SP_VOICECHAT_SHOWFACES= "robot_voicechat_showfaces";

    //语音加强方向 /**唤醒Mic序号**/
    public static final String SP_BEAM="beam";

    /**
     * 机构名称
     */
    public static final String SP_ORGANIZATION_NAME="organization_mame";

    /**
     * cms按钮
     */
    public static final String SP_CMS_NAME="spcmsname";
    /**
     * 默认的语音识别的模式（云端识别，识别效率会更高，不过受用户的宽带影响）,这个值可以通过管理后台来修改这个模式(所以没有加final)，默认是云端
     */
    //public static String VOICEREG_MODE = SpeechConstant.TYPE_CLOUD;//既然默认是云端，这里就不设这个变量了

    /**下面几个变量是定义录制的用户说的音频文件的位置**/
    public static String VOICE_FILE_PARENT = "epsit/";

    public static String VOICE_FILE_FILENAME = VOICE_FILE_PARENT+"wavaudio.wav";

    public static String VOICE_BACK_PATH=VOICE_FILE_PARENT+"voice_bak/";

    /**baseUrl中api网络请求的端口**/
    public static String API_PORT="api_port";

    /**语音接口的请求端口**/
    public static String VOICE_PORT = "voice_port";

    /**机器人所属厂商**/
    public static String ROBOT_MANUFACTURER= "robot_manufacturer";

    /**机器人型号**/
    public static String ROBOT_MODEl = "robot_model";

    /**定义电量低于20%就是低电量*/
    public static int BATTERY_LOWBATTERY = 20;

    //网络连接超时时间
    public static int CONNECT_TIMEOUT = 25;


    //2018-1-3 新加的两个字段
    //头部的网络是否ok
    public static String isHeadNetOk = "isHeadNetOk";

    //机器人电量
    public static String ROBOT_POWER = "power";

    //机器人gpu的ssid
    public static String ROBOT_GPU_SSID = "ssid";

    //ssid pwd
    public static String ROBOT_GPU_SSID_PWD = "ssid_pwd";
}
