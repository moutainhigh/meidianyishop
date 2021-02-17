package com.meidianyi.shop.service.pojo.shop.doctor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.pojo.shop.message.DoctorMessageCountVo;
import lombok.Data;

import java.util.List;

/**
 * @author 赵晓东
 * @description 医师端首页信息
 * @create 2020-07-28 13:47
 **/

@Data
public class DoctorMainShowVo {

    /**
     * 医师端首页消息提示
     */
    private DoctorMessageCountVo doctorMessageCountVo;

    /**
     * 医师端首页个人信息
     *
     */
    private String name;
    private Byte      sex;
    private String    url;
    @JsonIgnore
    private Byte      duty;
    private Integer   age;
    private String    registerHospital;
    private String    treatDisease;
    private String doctorTitle;

    /**
     * 医师科室名称
     */
    private List<String> departmentName;
    /**
     * 本月数据
     */
    private DoctorAttendanceVo doctorMonthData;


}
