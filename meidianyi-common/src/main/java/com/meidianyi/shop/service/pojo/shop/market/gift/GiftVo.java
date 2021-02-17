package com.meidianyi.shop.service.pojo.shop.market.gift;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 赠品活动出参
 *
 * @author 郑保乐
 */
@Data
public class GiftVo {

    private Integer id;
    /** 活动名称 **/
    private String name;
    /** 优先级 **/
    private Short level;
    /** 活动规则说明 **/
    private String explain;
    /** 赠品规则 **/
    private RuleVo rules;
    /** 开始时间 **/
    private Timestamp startTime;
    /** 结束时间 **/
    private Timestamp endTime;
    /** 参与活动商品 **/
    private List<Integer> goodsIds;
    /** 赠品 **/
    private List<ProductVo> gifts;
    /** 活动状态 **/
    private Byte status;

    @JsonIgnore
    private String goodsId;
    @JsonIgnore
    private String rule;
}
