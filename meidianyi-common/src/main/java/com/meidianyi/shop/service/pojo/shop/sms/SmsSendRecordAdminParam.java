package com.meidianyi.shop.service.pojo.shop.sms;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 赵晓东
 * @description
 * @create 2020-07-27 11:45
 **/

@Data
public class SmsSendRecordAdminParam {
    private Integer id;
    private String accountName;
    private Integer userId;
    private String mobile;
    private String requestMsg;
    private String responseCode;
    private String responseMsg;
    private String ext;
    private String sms;
    private String    responseMsgCode;
    private Timestamp createTime;
    private Timestamp updateTime;
    /**
     * 条件查询时间下界
     */
    private Timestamp startCreateTime;
    /**
     * 条件查询时间上界
     */
    private Timestamp endCreateTime;

    /**
     * 分页查询参数
     */
    private Integer currentPage;
    private Integer pageRows;
}
