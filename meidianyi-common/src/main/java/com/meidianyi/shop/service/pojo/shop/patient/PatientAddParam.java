package com.meidianyi.shop.service.pojo.shop.patient;

import com.meidianyi.shop.common.pojo.shop.table.PatientDo;
import lombok.Data;

/**
 * @author chenjie
 * @date 2020年07月28日
 */
@Data
public class PatientAddParam extends PatientDo {
    private Integer userId;
}
