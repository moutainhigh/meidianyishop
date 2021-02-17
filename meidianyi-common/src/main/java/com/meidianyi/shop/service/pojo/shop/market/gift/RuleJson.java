package com.meidianyi.shop.service.pojo.shop.market.gift;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 赠品策略 json
 *
 * @author 郑保乐
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleJson {

    /** 满金额 **/
    private String fullPrice;
    /** 满件数 **/
    private String fullNumber;
    /** 持卡 **/
    private String cardId;
    /** 具备标签 **/
    private String tagId;
    /** 支付开始时间 **/
    private Timestamp payStartTime;
    /** 支付结束时间 **/
    private Timestamp payEndTime;
    /** 支付排名 **/
    private String payTop;
    /** 最少下单次数 **/
    private String minPayNum;
    /** 最多下单次数 **/
    private String maxPayNum;
    /** 用户类型 **/
    private String userAction;
}
