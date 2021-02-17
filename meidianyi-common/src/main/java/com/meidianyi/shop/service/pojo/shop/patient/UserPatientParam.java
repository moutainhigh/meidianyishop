package com.meidianyi.shop.service.pojo.shop.patient;

import lombok.Data;

/**
 * @author chenjie
 */
@Data
public class UserPatientParam {
    private Integer   userId;
    private Integer   patientId;
    private Byte   isFetch;

    @Override
    public String toString() {
        return "UserPatientParam{" +
            "userId=" + userId +
            ", patientId=" + patientId +
            ", isFetch=" + isFetch +
            '}';
    }
}
