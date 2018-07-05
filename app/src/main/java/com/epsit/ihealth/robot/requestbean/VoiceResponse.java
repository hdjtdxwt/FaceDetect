package com.epsit.ihealth.robot.requestbean;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/5/005.
 */


public class VoiceResponse {
    /**
     * code : 200
     * message : 急诊药房请您出了门诊大厅右转直走，到急诊楼房
     * data : [{"id":"79f018d1ac65475aad7cdd6ef6fceb2e","robotId":"ttyy_robot_01","problem":"抽血抽血去哪儿","answer":"CT、抽血在门诊大楼东边，请沿大厅地下蓝色标示线走","tradingType":1}]
     * pageNum : 1
     * pageSize : 10
     * total : 0
     * pages : 0
     * dataType : null
     * knowledgeType : 2
     */

    private String code;
    private String message;
    private int pageNum;
    private int pageSize;
    private int total;
    private int pages;
    private String dataType;
    private String knowledgeType;

    //排班
    private String week;
    private String depar;

    private int faceData;    //整数		脸部表情
    private int armData;    //整数		手臂动作
    static Gson gson = new Gson();
    /**
     * 2018-1-12发现返回的数据是如下的：
     * {
     * "code": "200",
     * "message": "李总办公室沿着这路直走10米就到了",
     * "data": null,
     * "pageNum": 1,
     * "pageSize": 10,
     * "total": 0,
     * "pages": 0,
     * "dataType": null,
     * "problem": "李总办公室怎么走",
     * "knowledgeType": "2",
     * "msgType": "2",
     * "faceData": 0,
     * "armData": 0,
     * "subcommandType": 0,
     * "robotCode": null,
     * "knowledgeId": "aa753ecfd13044edacc1a4339e69b34d",
     * "week": null,
     * "depar": null,
     * "responseType": "0",
     * "jumpPage": null,
     * "canMove": true,
     * "locationX": "238",
     * "locationY": "106",
     * "angle": "10"
     * }
     */
    //2018-1-12在1.9服务请求后发现多加了几个需要用的字段---------如果外网没发布，用这个类可能报错说有字段找不到哦-----start---
    private String responseType;//: "0",
    private String jumpPageType;//哪一种类型的跳转，cms跳转，还是普通界面跳转
    private String jumpPage;//": null,
    private String canMove;//": true,
    private String locationX;//": "238",
    private String locationY;//": "106",
    private String angle;//": "10"
    //2018-1-12在1.9服务请求后发现多加了几个需要用的字段(主要是坐标位置)---------如果外网没发布，用这个类可能报错说有字段找不到哦-----end


    public String getJumpPageType() {
        return jumpPageType;
    }

    public void setJumpPageType(String jumpPageType) {
        this.jumpPageType = jumpPageType;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getJumpPage() {
        return jumpPage;
    }

    public void setJumpPage(String jumpPage) {
        this.jumpPage = jumpPage;
    }

    public String getCanMove() {
        return canMove;
    }

    public void setCanMove(String canMove) {
        this.canMove = canMove;
    }

    public String getLocationX() {
        return locationX;
    }

    public void setLocationX(String locationX) {
        this.locationX = locationX;
    }

    public String getLocationY() {
        return locationY;
    }

    public void setLocationY(String locationY) {
        this.locationY = locationY;
    }

    public String getAngle() {
        return angle;
    }

    public void setAngle(String angle) {
        this.angle = angle;
    }

    public boolean isReplace() {
        return isReplace;
    }

    public void setReplace(boolean replace) {
        isReplace = replace;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDepar() {
        return depar;
    }

    public void setDepar(String depar) {
        this.depar = depar;
    }

    private List<DataMap> data;
//    private JsonArray data =new JsonArray();

    private boolean isReplace = false; //是否替换过敏感词

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        /*if (!isReplace) {
            setMessage(ReplaceWord.getInstance().lookupReplace(message));
            isReplace = true;
        }*/
        return message;
    }

    public int getFaceData() {
        return faceData;
    }

    public void setFaceData(int faceData) {
        this.faceData = faceData;
    }

    public int getArmData() {
        return armData;
    }

    public void setArmData(int armData) {
        this.armData = armData;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getKnowledgeType() {
        return knowledgeType;
    }

    public void setKnowledgeType(String knowledgeType) {
        this.knowledgeType = knowledgeType;
    }

    public List<DataBean> getData() {
//        DataBean da=new Gson().fromJson(data,DataBean.class);
        Gson gson = new Gson();
        List<DataBean> rs = new ArrayList<DataBean>();
        Type type = new TypeToken<ArrayList<DataBean>>() {
        }.getType();
//        rs=gson.fromJson(data, type);
        return rs;
    }

    //
    public void setData(List<DataMap> data) {
//        this.data = data;
    }

    public List<DataMap> getDataMap() {
//        if(mapList == null || mapList.size()<=0 ) {
//            LogUtil.e("yu","解析来了 ------------------------");
//            Gson gson = new Gson();
//            mapList = new ArrayList<DataMap>();
//            Type type = new TypeToken<ArrayList<DataMap>>() {
//            }.getType();
//            mapList = gson.fromJson(data, type);
//            return mapList;
//        }
//        return mapList;
        return data;
    }

    //
    public void setDataMap(List<DataMap> da) {
        this.data = da;
    }

    public static class DataMap {
        private String data;  //分诊的

        private String poiUrl; //外部地图
        private String qrCodeUrl; //外部地图二维码连接


        //------------------
        private String visitExpert; //医生
        private String department; //科室
        private String departmentName; //科室名称
        private String doctorTitle; //技术职称
        private String feature; //特长   "
        private String registrationFee; //挂号费
        private String scheduleTime; //周一上午,周三上午,周四上午",时间
        private String scheduleType; //普通门诊"类型
        //------------------

        private String work;


        //------------ 地图显示的
        private String lindId; //线路的图片id
        private String mapId;
        private int type;
        private int coordinateX;
        private int coordinateY;
        private String mapName;
        private String imgPath;
        public Bitmap bitmap;
        private int entX;
        private int entY;
        private boolean isBit = false;
        //------------

        public String getLindId() {
            return lindId;
        }

        public void setLindId(String lindId) {
            this.lindId = lindId;
        }

        public String getPoiUrl() {
            return poiUrl;
        }

        public void setPoiUrl(String poiUrl) {
            this.poiUrl = poiUrl;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String imgPath) {
            this.imgPath = imgPath;
        }

        public int getEntX() {
            return entX;
        }

        public void setEntX(int entX) {
            this.entX = entX;
        }

        public String getWork() {
            return work;
        }

        public void setWork(String work) {
            this.work = work;
        }

        /**
         * 根据周几上班转换为上午 下午 全天
         * 1 上午 2 下午 3 全天
         *
         * @return
         */
        public int upDay(String work) {
            this.work = work;
            boolean time1 = false;
            boolean time2 = false;
            String se[] = scheduleTime.split(",");
            for (String t : se) {
                if (t.equals(work + "上午")) {
                    time1 = true;
                } else if (t.equals(work + "下午")) {
                    time2 = true;
                }
            }
            if (time1 && time2) {
                return 3;
            }
            if (time1) {
                return 1;
            } else {
                return 2;
            }

        }

        public int getEntY() {
            return entY;
        }

        public void setEntY(int entY) {
            this.entY = entY;
        }

        public boolean isBit() {
            return isBit;
        }

        public String getMapName() {
            return mapName;
        }

        public void setMapName(String mapName) {
            this.mapName = mapName;
        }

        public void setBit(boolean bit) {
            isBit = bit;
        }

        public String getMapId() {
            return mapId;
        }

        public void setMapId(String mapId) {
            this.mapId = mapId;
        }


        public String getQrCodeUrl() {
            return qrCodeUrl;
        }

        public void setQrCodeUrl(String qrCodeUrl) {
            this.qrCodeUrl = qrCodeUrl;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }

        public void setCoordinateX(int coordinateX) {
            this.coordinateX = coordinateX;
        }

        public int getCoordinateX() {
            return coordinateX;
        }

        public void setCoordinateY(int coordinateY) {
            this.coordinateY = coordinateY;
        }

        public int getCoordinateY() {
            return coordinateY;
        }


        public String getVisitExpert() {
            return visitExpert;
        }

        public void setVisitExpert(String visitExpert) {
            this.visitExpert = visitExpert;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

        public String getDoctorTitle() {
            return doctorTitle;
        }

        public void setDoctorTitle(String doctorTitle) {
            this.doctorTitle = doctorTitle;
        }

        public String getFeature() {
            return feature;
        }

        public void setFeature(String feature) {
            this.feature = feature;
        }

        public String getRegistrationFee() {
            return registrationFee;
        }

        public void setRegistrationFee(String registrationFee) {
            this.registrationFee = registrationFee;
        }

        public String getScheduleTime() {
            return scheduleTime;
        }

        public void setScheduleTime(String scheduleTime) {
            this.scheduleTime = scheduleTime;
        }

        public String getScheduleType() {
            return scheduleType;
        }

        public void setScheduleType(String scheduleType) {
            this.scheduleType = scheduleType;
        }

        public int getX() {
            if (getCoordinateX() < getEntX()) {
                return getCoordinateX() + (getEntX() - getCoordinateX());
            } else {
                return getEntX() + (getCoordinateX() - getEntX());
            }
        }

        public int getY() {
            if (getCoordinateY() < getEntY()) {
                return getCoordinateY() + (getEntY() - getCoordinateY());
            } else {
                return getEntY() + (getCoordinateY() - getEntY());
            }
        }

        @Override
        public String toString() {
            return gson.toJson(this);
        }
    }

    public static class DataBean {
        /**
         * id : 79f018d1ac65475aad7cdd6ef6fceb2e
         * robotId : ttyy_robot_01
         * problem : 抽血抽血去哪儿
         * answer : CT、抽血在门诊大楼东边，请沿大厅地下蓝色标示线走
         * tradingType : 1
         */

        private String id;
        private String robotId;
        private String problem;
        private String answer;
        private int tradingType;
        private boolean isReplace = false; //是否替换过敏感词


        public String getId() {
            return id;
        }


        public String getRobotId() {
            return robotId;
        }

        public void setRobotId(String robotId) {
            this.robotId = robotId;
        }

        public String getProblem() {
            return problem;
        }

        public void setProblem(String problem) {
            this.problem = problem;
        }

        public String getAnswer() {
            /*if (!isReplace) {
                setAnswer(ReplaceWord.getInstance().lookupReplace(answer));
                isReplace = true;
            }*/
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public int getTradingType() {
            return tradingType;
        }

        public void setTradingType(int tradingType) {
            this.tradingType = tradingType;
        }

        @Override
        public String toString() {
            return gson.toJson(this);
        }
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
