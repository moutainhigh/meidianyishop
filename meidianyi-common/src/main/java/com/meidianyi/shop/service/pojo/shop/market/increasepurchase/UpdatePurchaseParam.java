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
public class UpdatePurchaseParam {
    /**
     * 加价购活动id
     */
    @NotNull
    private Integer id;
    /**
     * 活动名称
     */
    private String name;
    /**
     * 活动起始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp startTime;
    /**
     * 活动结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp endTime;
    /**
     * 活动优先级
     */
    private Short level;
    /**
     * 单笔最大换购数量
     */
    private Short maxChangePurchase;
    /**
     * 主商品列表字符串，逗号分隔
     */
    private String goodsId;
    /**
     * 加价购规则定义
     */
    private List<PurchaseRule> rules;
    /**
     * 换购商品运费策略，0免运费，1使用原商品运费模板
     */
    private Byte redemptionFreight;
}
