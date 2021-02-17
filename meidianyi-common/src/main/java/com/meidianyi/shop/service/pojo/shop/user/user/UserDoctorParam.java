package com.meidianyi.shop.service.pojo.shop.user.user;

import lombok.Data;

/**
 * @author chenjie
 * @date 2020年08月11日
 */
@Data
public class UserDoctorParam {
    private Integer userId;
    private Integer doctorId;
    private Integer patientId;
    /**
     * 取消/关注
     */
    private Byte status;
}
