package com.meidianyi.shop.service.pojo.shop.rebate;

import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderRebateDo;
import lombok.Data;

/**
 * @author yangpengcheng
 * @date 2020/8/26
 **/
@Data
public class InquiryOrderRebateVo extends InquiryOrderRebateDo {
    private String doctorName;
    private String mobile;
    private String userName;
    /**
     * 问诊订单状态
     */
    private Integer orderStatus;
    /**
     * 患者姓名
     */
    private String patientName;
}
