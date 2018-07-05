package com.epsit.ihealth.robot.requestbean;

/**
 * Created by Administrator on 2018/7/5/005.
 */

public class BaseResponse<T> {

    /**
     * code : 200
     * message : 成功
     * data : {}
     * pageNum : 1
     * pageSize : 10
     * total : 0
     * pages : 0
     */

    protected String code;
    protected String message;
    protected T data;
    protected int pageNum;
    protected int pageSize;
    protected int total;
    protected int pages;

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

    public T getData() {
        return data ;
    }
}
