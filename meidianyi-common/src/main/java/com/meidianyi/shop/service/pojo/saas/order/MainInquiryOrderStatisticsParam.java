package com.meidianyi.shop.service.pojo.saas.order;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author yangpengcheng
 * @date 2020/8/14
 **/
@Data
public class MainInquiryOrderStatisticsParam extends BasePageParam {
    private String doctorName;
    private Timestamp startTime;
    private Timestamp endTime;
    private Integer shopId;
}
