package com.meidianyi.shop.service.pojo.wxapp.order.inquiry;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author yangpengcheng
 */
@Data
public class InquiryOrderListParam extends BasePageParam {
    /**
     * 订单状态
     */
    private Byte orderStatus;
    /**
     * 医师id
     **/
    private Integer doctorId;
    /**
     * 医师姓名
     */
    private String doctorName;
    /**
     * 科室id
     */
    private Integer departmentId;
    /**
     * 患者姓名
     */
    private String patientName;
    /**
     * 开始时间
     */
    private Timestamp startTime;
    /**
     * 结束时间
     */
    private Timestamp endTime;
}
