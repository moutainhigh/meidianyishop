package com.meidianyi.shop.service.pojo.shop.market.fullcut;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-08-09 15:30
 **/
@Data
public class MrkingStrategyAddParam {

    /** 活动名称 */
    @NotNull
    private String actName;

    /** 类型,1每满减 2满件 3满折 4仅第X件打折 */
    @NotNull
    private Byte type;

    /** 活动类型，0-全部商品参与活动；1-选中商品参与活动（由商品、平台分类、商家分类、品牌等ID字符串指定） */
    @NotNull
    private Byte actType;

    /** 满折满减优惠规则数组 */
    @NotNull
    private MrkingStrategyCondition[] conditionAddParams;

    /** 活动开始时间 */
    @NotNull
    private Timestamp startTime;

    /** 活动结束时间 */
    @NotNull
    private Timestamp endTime;

    /** 指定商品可用,商品ID列表串，逗号分隔 */
    private String recommendGoodsId;

    /** 指定平台分类可用 ,平台分类ID列表串，逗号分隔*/
    private String recommendCatId;

    /** 指定商家分类可用,商家分类ID列表串，逗号分隔 */
    private String recommendSortId;

    /** 指定品牌可用 ,品牌ID列表串，逗号分隔*/
    private String recommendBrandId;

    /** 持会员卡用户可参与，卡ID字符串，逗号分隔；为空时代表该活动所有人都可以参与 */
    private String cardId;

    /** 促销活动优先级 */
    @NotNull
    private Integer strategyPriority;

}
