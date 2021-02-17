package com.meidianyi.shop.service.pojo.shop.doctor;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import lombok.Data;

/**
 * @author chenjie
 * @date 2020年09月21日
 */
@Data
public class DoctorAttendanceListParam extends BasePageParam {
    private Byte type;
    private Integer doctorId;
}
