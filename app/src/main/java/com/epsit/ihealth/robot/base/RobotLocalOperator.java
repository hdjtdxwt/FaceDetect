package com.epsit.ihealth.robot.base;

/**
 * Created by Administrator on 2018/7/5/005.
 */


import android.content.Context;
import android.content.SharedPreferences;

import com.epsit.ihealth.robot.entity.CmsData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 本地存储数据操作，机器人信息保存在本地SharedPreferences里的操作
 * Created by Administrator on 2017/5/27 0027.
 */

public class RobotLocalOperator {

    private static volatile RobotLocalOperator instance;

    /**
     * 构造函数，在里面初始化了 SharedPreferences sp 对象
     */
    private RobotLocalOperator(){
    }

    /**
     * @return  获取RobotLocalOperator单例对象
     */
    public static synchronized RobotLocalOperator getInstance(){
        if(instance==null){
            instance = new RobotLocalOperator();
        }
        return instance;
    }
    /**
     * @return  获取SharedPreferences里保存的机器人的登陆id
     */
    public String getRobotId(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp.getString(AppConstant.SP_ROBOTID,"");
    }

    /**
     * 保存机器人登陆id到SharedPreferences里
     * @param id 登陆id
     */
    public void setRobotId(String id){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AppConstant.SP_ROBOTID,id);
        editor.commit();
    }

    /**
     * 获取SharedPreferences里保存的机器人的登陆密码
     * @return 机器人的登陆密码
     */
    public String getRobotSecret(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp.getString(AppConstant.SP_ROBOT_SECRET,"");
    }
    /**
     * 保存机器人登录密码到SharedPreferences里
     * @param secret 登录密码
     */
    public void setRobotSecret(String secret){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AppConstant.SP_ROBOT_SECRET,secret);
        editor.commit();
    }

    /**
     * @return 获取SharedPreferences里保存的token
     */
    public String getAccessToken(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp.getString(AppConstant.SP_ACCESS_TOKEN,"");
    }
    /**
     * 保存机器人token到SharedPreferences里
     * @param token 访问token
     */
    public void setAccessToken(String token){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AppConstant.SP_ACCESS_TOKEN,token);
        editor.commit();
    }
    /**
     * 获取机器人的token的有效时长（单位是秒）
     */
    public long getExpiresTime(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp==null? 0L : sp.getLong(AppConstant.SP_TOKEN_EXPIRES,0L);
    }
    /**
     * 保存机器人的token的有效时长
     * @param delayTime token有效时长，后台返回的单位是秒，1秒=1000毫秒，温馨提示：System.currentTimeMillis()得到的是毫秒值哦
     */
    public void setExpiresTime(long delayTime){

        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(AppConstant.SP_TOKEN_EXPIRES,delayTime);
        editor.commit();
    }

    /**
     * 保存服务器的系统时间到SharedPreferences里
     * @param systime 访问token
     */
    public void setServerSystime(long systime){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(AppConstant.SP_SERVER_SYSTIME,systime);
        editor.commit();
    }
    /**
     * 获取机器人的token的有效时长（单位是秒）
     */
    public long getServerSystime(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp==null? 0L : sp.getLong(AppConstant.SP_SERVER_SYSTIME,0L);
    }

    /**
     * 是否要在用户交流对话的时候，将用户的语音保存成文件
     * @return
     */
    public boolean isSaveVoiceFile(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp==null? false : sp.getBoolean(AppConstant.SP_VOICEFILE_SAVE,true);
    }
    public void setIsSaveVoiceFile(boolean flag){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(AppConstant.SP_VOICEFILE_SAVE,flag);
        editor.commit();
    }

    public void setExit(boolean flag){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_HASEXIT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(AppConstant.SP_VOICEFILE_SAVE,flag);
        editor.commit();
    }

    /**
     * 应用是否已经退出，登陆错误重试将不再重试
     * @return
     */
    public boolean getHasExit(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp==null? false : sp.getBoolean(AppConstant.SP_HASEXIT,true);
    }


    /**
     * @return 获取SharedPreferences里保存的旧的userId(userId是机器人识别到的人脸后，给机器人去的一个名)
     */
    public String getOldUserId(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp.getString(AppConstant.SP_OLDUSERID,"");
    }
    /**
     * 保存机器人识别到的就的userId到SharedPreferences里
     */
    public void setOldUserId(String userId){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AppConstant.SP_OLDUSERID,userId);
        editor.commit();
    }

    /**
     * @return 获取SharedPreferences里保存的新的userId(userId是机器人识别到的人脸后，给机器人去的一个名)
     */
    public String getNewUserId(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_NEWUSERID, Context.MODE_PRIVATE);
        return sp.getString(AppConstant.SP_NEWUSERID,"");
    }
    /**
     * 保存机器人识别到的新的userId到SharedPreferences里
     */
    public void setNewUserId(String userId){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_NEWUSERID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AppConstant.SP_NEWUSERID,userId);
        editor.commit();
    }

    /**
     */
    public String getNewUserPhoto(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp.getString(AppConstant.SP_NEWUSER_PHOTO,"");
    }
    /**
     */
    public void setNewUserPhoto(String path){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AppConstant.SP_NEWUSER_PHOTO,path);
        editor.commit();
    }


    /**
     * 是否要在用户首页显示用户头像
     * @return
     */
    public boolean isMainShowFaces(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp==null? false : sp.getBoolean(AppConstant.SP_MAIN_SHOWFACES,true);
    }
    public void setIsMainShowFaces(boolean flag){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(AppConstant.SP_MAIN_SHOWFACES,flag);
        editor.commit();
    }
    /**
     * 是否要在聊天界面显示人脸识别的图像
     * @return
     */
    public boolean isVoiceShowFaces(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp==null? false : sp.getBoolean(AppConstant.SP_VOICECHAT_SHOWFACES,true);
    }
    public void setIsVoiceShowFaces(boolean flag){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(AppConstant.SP_VOICECHAT_SHOWFACES,flag);
        editor.commit();
    }

    public int getApiPort(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp.getInt(AppConstant.API_PORT,8088);
    }
    /**
     * 保存 api端口 到SharedPreferences里
     * @param port api端口
     */
    public void setApiPort(int port){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(AppConstant.API_PORT,port);
        editor.commit();
    }

    public int getVoicePort(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp.getInt(AppConstant.VOICE_PORT,8084);
    }
    /**
     * 保存 api端口 到SharedPreferences里
     * @param port api端口
     */
    public void setVoicePort(int port){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(AppConstant.VOICE_PORT,8084);
        editor.commit();
    }


    /**
     * 保存 manufacturer
     * @param port api端口
     */
    public void setManufacturer(String port){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AppConstant.ROBOT_MANUFACTURER,port);
        editor.commit();
    }

    /** 没有返回回“” **/
    public String getManufacturer(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp.getString(AppConstant.ROBOT_MANUFACTURER,"");
    }


    /**
     * 保存 ROBOT_MODEl
     * @param port api端口
     */
    public void setModel(String port){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AppConstant.ROBOT_MODEl,port);
        editor.commit();
    }

    /** 没有返回回“” **/
    public String getModel(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp.getString(AppConstant.ROBOT_MODEl,"");
    }

    /**
     * 语音增强方向
     * **唤醒Mic序号**
     * @return 默认前方加强
     */
    public String getBeam(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp.getString(AppConstant.SP_BEAM,"BEAM 0");
    }
    /**
     * 语音增强方向 存到SharedPreferences里
     * **唤醒Mic序号**
     * @param beam 方向
     */
    public void setBeam(String beam){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AppConstant.SP_BEAM,beam);
        editor.commit();
    }

    /**
     * 机构名称
     * @return
     */
    public String getOrganizationName(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp.getString(AppConstant.SP_ORGANIZATION_NAME,"");
    }

    /**
     * 机构名称
     * @param name
     */
    public void setOrganizationName(String name){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AppConstant.SP_ORGANIZATION_NAME,name);
        editor.commit();
    }

    /**
     * 保存cms
     * @param set
     */
    public void setCms(List<CmsData> set){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(set);
        editor.putString(AppConstant.SP_CMS_NAME,json);
        editor.commit();
        getCms();
    }

    /**
     * 保存cms
     */
    public List<CmsData> getCms(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        String str = sp.getString(AppConstant.SP_CMS_NAME,"");
        Gson gson = new Gson();
        Type type = new TypeToken<List<CmsData>>() {
        }.getType();
        List<CmsData> arrayList = gson.fromJson(str,type);
        return arrayList;
    }

    /**
     * 设置当前机器人的电量
     * @param power 机器人电量百分比，比如50%，会存一个50，50以字符串的形式保存
     */
    public void setPower(String power){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AppConstant.ROBOT_POWER,power);
        editor.commit();
    }

    public String getPower(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp.getString(AppConstant.ROBOT_POWER,"");
    }

    /**
     * 设置头部网络是否ok
     * @param flag
     */
    public void setIsHeadNetOk(boolean flag){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(AppConstant.isHeadNetOk,flag);
        editor.commit();
    }
    public boolean getIsHeadNetOk(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp.getBoolean(AppConstant.isHeadNetOk,false);
    }

    /**
     * @return ssid
     */
    public String getSsid(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp.getString(AppConstant.ROBOT_GPU_SSID,"");
    }
    /**
     * @param ssid 方向
     */
    public void setSsid(String ssid){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AppConstant.ROBOT_GPU_SSID,"");
        editor.commit();
    }

    /**
     * @return ssid
     */
    public String getSsidPwd(){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        return sp.getString(AppConstant.ROBOT_GPU_SSID_PWD,"");
    }
    /**
     * @param ssidPwd 方向
     */
    public void setSsidPwd(String ssidPwd){
        SharedPreferences sp = RobotApplication.getInstance().getSharedPreferences(AppConstant.SP_ROBOTINFO_FNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AppConstant.ROBOT_GPU_SSID_PWD,"");
        editor.commit();
    }
}
