package com.meidianyi.shop.service.pojo.shop.message;

import lombok.Data;

/**
 * @author 赵晓东
 * @description
 * @create 2020-07-24 14:35
 **/

@Data
public class DoctorMessageCountVo {

    /**
     * 待问诊数量
     */
    private Integer notImSessionCount;

    /**
     * 问诊列表是否有未读
     */
    private Boolean alreadyImSessionCount;

    /**
     * 待开方数量
     */
    private Integer notOrderInfoCount;

    /**
     * 开方列表是否有未读
     */
    private Boolean alreadyOrderInfoCount;

    /**
     * 待续方数量
     */
    private Integer notOrderGoodsCount;

    /**
     * 续方列表是否有未读
     */
    private Boolean alreadyPrescription;
}
