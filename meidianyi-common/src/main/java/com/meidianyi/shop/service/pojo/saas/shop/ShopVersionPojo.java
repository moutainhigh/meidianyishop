/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.service.pojo.saas.shop;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ShopVersionPojo implements Serializable {

    private static final long serialVersionUID = -1777289951;

    private Integer   id;
    private String    versionName;
    private String    level;
    private String    content;
    private Timestamp created;
    private Timestamp updateTime;
    private String    desc;
    private Byte      flag;

    public ShopVersionPojo() {}

    public ShopVersionPojo(ShopVersionPojo value) {
        this.id = value.id;
        this.versionName = value.versionName;
        this.level = value.level;
        this.content = value.content;
        this.created = value.created;
        this.updateTime = value.updateTime;
        this.desc = value.desc;
        this.flag = value.flag;
    }

    public ShopVersionPojo(
        Integer   id,
        String    versionName,
        String    level,
        String    content,
        Timestamp created,
        Timestamp updateTime,
        String    desc,
        Byte      flag
    ) {
        this.id = id;
        this.versionName = versionName;
        this.level = level;
        this.content = content;
        this.created = created;
        this.updateTime = updateTime;
        this.desc = desc;
        this.flag = flag;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVersionName() {
        return this.versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreated() {
        return this.created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Byte getFlag() {
        return this.flag;
    }

    public void setFlag(Byte flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ShopVersionPojo (");

        sb.append(id);
        sb.append(", ").append(versionName);
        sb.append(", ").append(level);
        sb.append(", ").append(content);
        sb.append(", ").append(created);
        sb.append(", ").append(updateTime);
        sb.append(", ").append(desc);
        sb.append(", ").append(flag);

        sb.append(")");
        return sb.toString();
    }
}