package com.meidianyi.shop.service.pojo.shop.market.gift;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

/**
 * 赠品活动入参
 *
 * @author 郑保乐
 */
@Data
public class GiftParam {

    private Integer id;
    /** 活动名称 **/
    @NotEmpty
    private String name;
    /** 优先级 **/
    @NotNull
    private Short level;
    /** 开始时间 **/
    @NotNull
    private Timestamp startTime;
    /** 结束时间 **/
    @NotNull
    private Timestamp endTime;
    /** 参与活动商品 **/
    private List<Integer> goodsIds;
    /** 活动规则说明 **/
    @NotEmpty
    private String explain;
    /** 赠品规则 **/
    @NotNull
    private RuleParam rules;
    /** 赠品 **/
    @NotEmpty
    private List<ProductParam> gifts;

    @JsonIgnore
    private String goodsId;
    @JsonIgnore
    private String rule;
}
