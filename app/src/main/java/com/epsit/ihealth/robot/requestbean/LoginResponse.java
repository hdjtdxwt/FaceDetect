package com.epsit.ihealth.robot.requestbean;

import com.epsit.ihealth.robot.entity.CmsData;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Administrator on 2017/5/12 0012.
 * 登陆成功后，后台返回的json数据转换成的实体bean
 */

public class LoginResponse {
    /**
     * code : 200
     * message : 成功
     * data : {"access_token":"A567687DC876DFEA5467C876DFEA5467","expires_in":7200}
     * pageNum : 1
     * pageSize : 10
     * total : 0
     * pages : 0
     */

    private DataBean data;
    private int pageNum;
    private int pageSize;
    private int total;
    private int pages;
    protected String code;
    protected String message;
    static Gson gson = new Gson();
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
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



    public static class DataBean {
        /**
         * access_token : A567687DC876DFEA5467C876DFEA5467
         * expires_in : 7200
         */
        public List<CmsData> cmsList;

        public List<CmsData> getCmsList() {
            return cmsList;
        }

        public void setCmsList(List<CmsData> cmsBeanList) {
            this.cmsList = cmsBeanList;
        }

        private String access_token;
        private int expires_in;
        private long system_time;

        private String organizationName; //机构名称
        private String manufacturer;  //机器人厂商
        private String mic_number;   //麦克风序号
        private String robot_typeid;  //机器人ID

        public String getOrganizationName() {
            return organizationName;
        }

        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getMic_number() {
            return mic_number;
        }

        public void setMic_number(String mic_number) {
            this.mic_number = mic_number;
        }

        public String getRobot_typeid() {
            return robot_typeid;
        }

        public void setRobot_typeid(String robot_typeid) {
            this.robot_typeid = robot_typeid;
        }

        public long getSystem_time() {
            return system_time;
        }

        public void setSystem_time(long system_time) {
            this.system_time = system_time;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public int getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(int expires_in) {
            this.expires_in = expires_in;
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
