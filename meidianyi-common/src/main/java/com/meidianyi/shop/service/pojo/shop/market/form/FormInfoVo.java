package com.meidianyi.shop.service.pojo.shop.market.form;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author liufei
 * @date 2019/8/7
 */
@Data
public class FormInfoVo {
    private Integer pageId;
    private String pageName;
    private Timestamp createTime;
    /**反馈数量*/
    private Integer submitNum;
    private Byte status;
    /**有效期 1永久有效，0期限内有效*/
    private Byte validityPeriod;
    private Timestamp startTime;
    private Timestamp endTime;
}
