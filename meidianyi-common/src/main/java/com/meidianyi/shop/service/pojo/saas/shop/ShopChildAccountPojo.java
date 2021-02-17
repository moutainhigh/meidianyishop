package com.meidianyi.shop.service.pojo.saas.shop;

import java.io.Serializable;
import java.sql.Timestamp;


@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ShopChildAccountPojo implements Serializable {

    private static final long serialVersionUID = -1663956208;

    private Integer   accountId;
    private Integer   sysId;
    private String    accountName;
    private Timestamp createTime;
    private String    mobile;
    private String    backlog;
    private String    officialOpenId;
    private Byte      isBind;

    public ShopChildAccountPojo() {}

    public ShopChildAccountPojo(ShopChildAccountPojo value) {
        this.accountId = value.accountId;
        this.sysId = value.sysId;
        this.accountName = value.accountName;
        this.createTime = value.createTime;
        this.mobile = value.mobile;
        this.backlog = value.backlog;
        this.officialOpenId = value.officialOpenId;
        this.isBind = value.isBind;
    }

    public ShopChildAccountPojo(
        Integer   accountId,
        Integer   sysId,
        String    accountName,
        String    accountPwd,
        Timestamp createTime,
        String    mobile,
        String    backlog,
        String    officialOpenId,
        Byte      isBind
    ) {
        this.accountId = accountId;
        this.sysId = sysId;
        this.accountName = accountName;
        this.createTime = createTime;
        this.mobile = mobile;
        this.backlog = backlog;
        this.officialOpenId = officialOpenId;
        this.isBind = isBind;
    }

    public Integer getAccountId() {
        return this.accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getSysId() {
        return this.sysId;
    }

    public void setSysId(Integer sysId) {
        this.sysId = sysId;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Timestamp getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBacklog() {
        return this.backlog;
    }

    public void setBacklog(String backlog) {
        this.backlog = backlog;
    }

    public String getOfficialOpenId() {
        return this.officialOpenId;
    }

    public void setOfficialOpenId(String officialOpenId) {
        this.officialOpenId = officialOpenId;
    }

    public Byte getIsBind() {
        return this.isBind;
    }

    public void setIsBind(Byte isBind) {
        this.isBind = isBind;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ShopChildAccountPojo (");

        sb.append(accountId);
        sb.append(", ").append(sysId);
        sb.append(", ").append(accountName);
        sb.append(", ").append(createTime);
        sb.append(", ").append(mobile);
        sb.append(", ").append(backlog);
        sb.append(", ").append(officialOpenId);
        sb.append(", ").append(isBind);

        sb.append(")");
        return sb.toString();
    }
}
