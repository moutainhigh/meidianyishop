package com.meidianyi.shop.service.pojo.shop.rebate;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author yangpengcheng
 * @date 2020/8/27
 **/
@Data
public class DoctorWithdrawListParam extends BasePageParam {
    private Integer doctorId;
    private String doctorName;
    private Byte status;
    private Timestamp startTime;
    private Timestamp endTime;
}
