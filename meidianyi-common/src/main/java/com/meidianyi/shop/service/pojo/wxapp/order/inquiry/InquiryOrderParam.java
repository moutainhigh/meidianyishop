package com.meidianyi.shop.service.pojo.wxapp.order.inquiry;

import lombok.Data;

/**
 * @author yangpengcheng
 * @date 2020/8/3
 **/
@Data
public class InquiryOrderParam {
    private Integer doctorId;
    private Integer departmentId;
    private Integer patientId;
    private Integer userId;
}
