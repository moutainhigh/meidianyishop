package com.meidianyi.shop.service.pojo.shop.prescription;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 孔德成
 * @date 2020/7/6 10:08
 */
@Data


public class PrescriptionListVo {


    /**
     * 患者id
     */
    private Integer patientId;
    /**
     * 患者名称
     */
    private String name;
    /**
     * 处方号
     */
    private String prescriptionCode;
    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 科室名称
     */
    private String departmentName;
    /**
     * 医师名称
     */
    private String doctorName;
    /**
     * 医师编码
     */
    private String doctorCode;
    /**
     * 医师id
     */
    private Integer doctorId;
    /**
     * 就诊时间
     */
    private Timestamp diagnoseTime;
    /**
     * 药师名称
     */
    private String pharmacistName;
    /**
     * 诊断名称
     */
    private String diagnosisName;
    /**
     * 诊断详情
     */
    private String diagnosisDetail;
    /**
     * 患者主诉
     */
    private String patientComplain;
    /**
     * 患者体征
     */
    private String patientSign;
    /**
     * 总价
     */
    private BigDecimal totalPrice = new BigDecimal(0);
    /**
     * 处方来源 0系统内部创建 1医院拉取
     */
    private Byte source;
    /**
     * 处方审核状态 0待审核 1审核通过 2审核未通过
     */
    private Byte status;
    /**
     * 处方审核医师评价
     */
    private String statusMemo;
    /**
     * 处方有效期类型 0:未知（默认过期），1:永久有效，2:时间段内有效
     */
    private Byte expireType;
    /**
     * 审核类型 处方审核类型 ''药品审核类型, 0不审核,1审核,2开方,3根据处方下单
     */
    private Byte auditType;
    /**
     * 结算标志：0：未结算，1：已结算
     */
    private Byte settlementFlag;
    /**
     * 是否有效  0无效 1有效，默认1
     */
    private Byte isValid;
    /**
     * 是否有效  0无效 1有效，默认1
     */
    private Byte isUsed;
    /**
     * 开方时间
     */
    private Timestamp prescriptionCreateTime;
    /**
     * 处方过期时间
     */
    private Timestamp prescriptionExpireTime;
    /**
     * 开方时间
     */
    private Timestamp createTime;

    private List<String> goodsNames;
}
