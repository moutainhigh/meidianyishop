package com.meidianyi.shop.service.pojo.shop.rebate;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author yangpengcheng
 * @date 2020/8/26
 **/
@Data
public class InquiryOrderRebateListParam extends BasePageParam {
    private String doctorName;
    private Integer doctorId;
    private Byte status;
    private Timestamp startTime;
    private Timestamp endTime;
}
