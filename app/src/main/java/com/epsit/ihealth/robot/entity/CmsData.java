package com.epsit.ihealth.robot.entity;

/**
 * Created by ${yyj} on 2017/8/17.
 * CMS 跳转连接
 * 如果有id 这字段 不能是String类型
 */

public class CmsData /*extends DataSupport*/{
    private String id;
    private String siteId;
    private String siteName;
    private String name;
    private String image;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
