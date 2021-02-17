package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

import java.util.List;

/**
 * @author chenjie
 */
@Data
public class DoctorConsultationParam{
    private String keyword;
    private Integer departmentId;
    private Integer titleId;
    private List doctorIds;
    private List departmentDoctorIds;
    private Integer currentPage=1;
    private Integer pageRows=20;
    private Integer userId=0;
    private Byte type=0;
    private List userDoctorIds;
    private Byte sortType=0;
}
