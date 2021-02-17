package com.meidianyi.shop.service.pojo.wxapp.medical.im;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2020年07月23日
 */
public class SessionTest{
    private Integer type;
    private List<String> orderSns;

    private Integer departmentId;
    private Integer   doctorId;
    private Integer   userId;
    private Integer   patientId;
    private String    orderSn;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<String> getOrderSns() {
        return orderSns;
    }

    public void setOrderSns(List<String> orderSns) {
        this.orderSns = orderSns;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }
}
