package com.meidianyi.shop.service.pojo.shop.prescription;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 处方简略对象
 * @author 孔德成
 * @date 2020/7/2 18:09
 */
@Data
public class PrescriptionSimpleVo  {

    /**
     * 处方号
     */
    private String    prescriptionCode;
    /**
     * 医师
     */
    private String    doctorName;
    /**
     * 开方日期
     */
    private Timestamp prescriptionCreateTime;
    /**
     * 科室名称
     */
    private String    departmentName;
    /**
     * 诊断名称
     */
    private String    diagnosisName;

//    private List<PrescriptionItemInfoVo> itemList;

}
