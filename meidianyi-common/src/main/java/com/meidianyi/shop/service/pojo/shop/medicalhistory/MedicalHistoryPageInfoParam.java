package com.meidianyi.shop.service.pojo.shop.medicalhistory;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import lombok.Data;

import java.util.List;

/**
 * @author 赵晓东
 * @description
 * @create 2020-07-09 18:50
 */
@Data
public class MedicalHistoryPageInfoParam extends BasePageParam {

    /**
     * 患者ID
     */
    private int patientId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 处方Nos
     */
    private List<String> prescriptionNos;
}
