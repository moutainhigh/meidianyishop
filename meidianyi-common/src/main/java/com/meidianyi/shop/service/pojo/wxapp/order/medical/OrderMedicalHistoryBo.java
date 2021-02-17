package com.meidianyi.shop.service.pojo.wxapp.order.medical;

import lombok.Data;

/**
 * @author 孔德成
 * @date 2020/7/28 14:25
 */
@Data
public class OrderMedicalHistoryBo {
    /**
     * 患者名称
     */
    private String patientName;
    /**
     * 患者id
     */
    private Integer patientId;
    /**
     * 性别
     */
    private Byte sex;
    /**
     * 年龄
     */
    private Integer age;
    /**
     *证件号
     */
    private String identityCode;
    /**
     *证件类型
     */
    private String identityType;
    /**
     *就诊卡号
     */
    private String patientTreatmentCode;
    /**
     * 患者主诉
     */
    private String patientComplain;
    /**
     * 历史诊断
     */
    private String diseaseHistory;
    /**
     * 自我描述
     */
    private String describe;
    /**
     * 过敏史
     */
    private String    allergyHistory;
    /**
     * 家族病史
     */
    private String    familyDiseaseHistory;
    /**
     * 妊娠哺乳状态
     */
    private Byte      gestationType;
    /**
     * 肾功能
     */
    private Byte      kidneyFunctionOk;
    /**
     * 肝功能
     */
    private Byte      liverFunctionOk;
    /**
     * 图片地址
     */
    private String imagesList;
    /**
     * 订单id
     */
    private Integer orderId;

}
