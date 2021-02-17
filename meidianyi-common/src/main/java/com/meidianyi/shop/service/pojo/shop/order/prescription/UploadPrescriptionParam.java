package com.meidianyi.shop.service.pojo.shop.order.prescription;

import lombok.Data;

import java.util.List;

/**1
 * 上传his的订单处方数据
 * @author 孔德成
 * @date 2020/7/16 15:29
 */
@Data
public class UploadPrescriptionParam {

    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 患者姓名
     */
    private String name;

    /**
     * 电话
     */
    private String mobile;
    /**
     * 患者身份证
     */
    private String identityCode;
    /**
     * 性别 0男1女
     */
    private Byte sex;
    /**
     * 疾病名称
     */
    private List<String> diagnosisNameList;
    /**
     * 药品列表
     */
    private List<UploadPrescriptionGoodsParam> goodsMedicalList;
    /**
     * 相关处方号
     */
    private List<String> prescriptionList;


}
