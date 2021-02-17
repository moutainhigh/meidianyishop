package com.meidianyi.shop.service.pojo.shop.department;

import lombok.Data;

/**
 * @author chenjie
 * @date 2020年09月14日
 */
@Data
public class DepartmentRecommendCfgParam {
    private Integer doctorRecommendType;
    private Integer departmentRecommendType;
    private Integer doctorRecommendConsultationRate;
    private Integer doctorRecommendInquiryRate;
    private Integer departmentRecommendConsultationRate;
    private Integer departmentRecommendInquiryRate;
    private Integer departmentRecommendDoctorRate;
}
