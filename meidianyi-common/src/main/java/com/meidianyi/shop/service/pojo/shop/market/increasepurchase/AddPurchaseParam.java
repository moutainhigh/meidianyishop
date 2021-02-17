package com.meidianyi.shop.service.pojo.shop.market.increasepurchase;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author liufei
 * @date 2019/8/14
 */
@Data
public class AddPurchaseParam {
    /**
     * 活动名称
     */
    @NotNull
    private String name;
    /**
     * 活动起始时间
     */
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp startTime;
    /**
     * 活动结束时间
     */
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp endTime;
    /**
     * 活动优先级
     */
    @NotNull
    private Short level;
    /**
     * 单笔最大换购数量
     */
    private Short maxChangePurchase;
    /**
     * 主商品列表字符串，逗号分隔
     */
    @NotNull
    private String goodsId;
    /**
     * 加价购规则定义
     */
    @NotNull
    private List<PurchaseRule> rules;
    /**
     * 换购商品运费策略，0免运费，1使用原商品运费模板
     */
    @NotNull
    private Byte redemptionFreight;

    /**
     * 是否给参加活动的用户打标签，1是
     */
    private Byte activityTag;
    /**
     * 参加活动打标签id列表
     */
    private List<Integer> activityTagId;

}
