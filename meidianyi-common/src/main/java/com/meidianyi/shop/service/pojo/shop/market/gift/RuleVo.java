package com.meidianyi.shop.service.pojo.shop.market.gift;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 赠品策略入参
 *
 * @author 郑保乐
 */
@Data
public class RuleVo {

    /** 满金额 **/
    private BigDecimal fullPrice;
    /** 满件数 **/
    private Integer fullNumber;
    /** 支付开始时间 **/
    private Timestamp payStartTime;
    /** 支付结束时间 **/
    private Timestamp payEndTime;
    /** 支付排名 **/
    private Integer payTop;
    /** 最少下单次数 **/
    private Integer minPayNum;
    /** 最多下单次数 **/
    private Integer maxPayNum;
    /** 用户类型 **/
    private Byte userAction;
    /** 持卡 **/
    private List<Integer> cardId;
    /** 具备标签 **/
    private List<Integer> tagId;
}
