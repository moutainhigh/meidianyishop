package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;

/**
 * @author chenjie
 * @date 2020年09月16日
 */
@Data
public class DoctorAttendanceDivideVo {
    private Integer halfNum;
    private Integer thirdQuarterNum;
    private Integer fourthQuarterNum;
}
