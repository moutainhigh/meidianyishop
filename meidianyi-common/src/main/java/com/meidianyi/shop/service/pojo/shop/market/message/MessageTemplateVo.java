package com.meidianyi.shop.service.pojo.shop.market.message;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author luguangyao
 */
@Data
public class MessageTemplateVo {
    /** 推送消息id */
    private Integer id;
    /** 消息名称 */
    private String name;
    /** 业务标题 */
    private String title;
    /** 开始日期 */
    private Timestamp startTime ;
    /** 已发送人数 */
    private Integer sentNumber;
    /** 回访数量 */
    private Integer clickedNumber;
    /** 回访率 */
    private Double percentage;
    /** 发送状态 */
    private Byte sendStatus;


}
