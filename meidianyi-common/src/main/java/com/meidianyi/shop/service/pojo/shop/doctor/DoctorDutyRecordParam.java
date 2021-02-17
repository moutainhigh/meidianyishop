package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

/**
 * @author chenjie
 * @date 2020年08月12日
 */
@Data
public class DoctorDutyRecordParam {
    private Integer doctorId;
    private Byte dutyStatus;
    private Byte type=1;
}
