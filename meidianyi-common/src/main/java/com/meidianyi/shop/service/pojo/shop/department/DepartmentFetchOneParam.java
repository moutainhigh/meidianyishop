package com.meidianyi.shop.service.pojo.shop.department;

import lombok.Data;

import java.util.Date;

/**
 * @author chenjie
 */
@Data
public class DepartmentFetchOneParam {
    private String departCode;
    private String departName;
    private Integer state;
    private Integer createTime;
    private Date lastUpdateTime;
    private String pCode;

    public String getDepartCode() {
        return departCode;
    }

    public void setDepartCode(String departCode) {
        this.departCode = departCode;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getpCode() {
        return pCode;
    }

    public void setpCode(String pCode) {
        this.pCode = pCode;
    }

    @Override
    public String toString() {
        return "DepartmentFetchOneParam{" +
            "departCode='" + departCode + '\'' +
            ", departName='" + departName + '\'' +
            ", state=" + state +
            ", createTime=" + createTime +
            ", lastUpdateTime=" + lastUpdateTime +
            ", pCode='" + pCode + '\'' +
            '}';
    }
}
