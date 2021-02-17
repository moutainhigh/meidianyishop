package com.meidianyi.shop.service.pojo.shop.patient;

import lombok.Data;

/**
 * @author chenjie
 */
@Data
public class PatientMoreInfoParam {
    private Integer   id;
    private String    name;
    private Byte      checked=0;
}
