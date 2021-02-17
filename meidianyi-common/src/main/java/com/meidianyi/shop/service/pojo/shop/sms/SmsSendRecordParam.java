package com.meidianyi.shop.service.pojo.shop.sms;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2020/7/24 9:25
 */
@Data
public class SmsSendRecordParam {

    private Integer id;
    private String accountName;
    private Integer userId;
    private String mobile;
    private String requestMsg;
    private String responseCode;
    private String responseMsg;
    private String responseMsgCode;
    private String ext;
    private String sms;
    private Timestamp createTime;
    private Timestamp updateTime;


}
