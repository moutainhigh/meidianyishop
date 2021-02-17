package com.meidianyi.shop.service.pojo.shop.distribution;

/**
 * 返利测试配置入参数类
 * user：常乐
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import javax.persistence.Column;
import java.sql.Timestamp;

/**
 * @author changle
 */
@Data
public class DistributionStrategyParam {
    @Column(name="id")
    private Integer   id;
    /** 策略名称*/
    @Column(name="strategy_name")
    private String    strategyName;
    /**策略等级*/
    @Column(name="strategy_level")
    private Byte      strategyLevel;
    /**开始时间*/
    @Column(name="start_time")
    private Timestamp startTime;
    /**结束时间*/
    @Column(name="end_time")
    private Timestamp endTime;
    /**自购返利 0:关闭；1：开启*/
    @Column(name="self_purchase")
    private Byte      selfPurchase;
    /** 成本保护 0：关闭；1：开启*/
    @Column(name="cost_protection")
    private Byte      costProtection;
    /**  返利比例*/
    @Column(name="fanli_ratio")
    private Double    fanliRatio;
    /**间接*/
    @Column(name="rebate_ratio")
    private Double    rebateRatio;
    /** 二级返利比例*/
    @JsonProperty("fanliRatio_2")
    @Column(name="fanli_ratio_2")
    private Double fanliRatio2;
    /**间接*/
    @JsonProperty("rebateRatio_2")
    @Column(name="rebate_ratio_2")
    private Double rebateRatio2;

    /**三级返利比例*/
    @JsonProperty("fanliRatio_3")
    @Column(name="fanli_ratio_3")
    private Double fanliRatio3;

    /**间接*/
    @JsonProperty("rebateRatio_3")
    @Column(name="rebate_ratio_3")
    private Double rebateRatio3;

    /**四级返利比例*/
    @JsonProperty("fanliRatio_4")
    @Column(name="fanli_ratio_4")
    private Double fanliRatio4;

    /**间接*/
    @JsonProperty("rebateRatio_4")
    @Column(name="rebate_ratio_4")
    private Double rebateRatio4;

    /**五级返利比例*/
    @JsonProperty("fanliRatio_5")
    @Column(name="fanli_ratio_5")
    private Double fanliRatio5;

    /**间接*/
    @JsonProperty("rebateRatio_5")
    @Column(name="rebate_ratio_5")
    private Double rebateRatio5;

    /**是否首单返利 0：关闭；1：开启*/
    @Column(name="first_rebate")
    private Byte firstRebate;
    /**首单返利比例*/
    @Column(name="first_ratio")
    private Double firstRatio;

    @JsonProperty("firstRatio_2")
    @Column(name="first_ratio_2")
    private Double firstRatio2;

    @JsonProperty("firstRatio_3")
    @Column(name="first_ratio_3")
    private Double firstRatio3;

    @JsonProperty("firstRatio_4")
    @Column(name="first_ratio_4")
    private Double firstRatio4;

    @JsonProperty("firstRatio_5")
    @Column(name="first_ratio_5")
    private Double firstRatio5;

    /**返利商品类型 0：全部商品；1：部分商品*/
    @Column(name="recommend_type")
    private Byte      recommendType;
    /** 返利商品ID*/
    @Column(name="recommend_goods_id")
    private String    recommendGoodsId;
    /**返利分类ID*/
    @Column(name="recommend_cat_id")
    private String    recommendCatId;
    /**状态 0：启用 ；1：停用*/
    @Column(name="status")
    private Byte      status;
    /** 删除 0：未删除；1：删除*/
    @Column(name="del_flag")
    private Byte      delFlag;
    /** 删除时间*/
    @Column(name="del_time")
    private Timestamp delTime;
    /** 返利商家分类IDs*/
    @Column(name="recommend_sort_id")
    private String    recommendSortId;
    /** 赠送优惠券 0：关闭；1：开启*/
    @Column(name="send_coupon")
    private Byte      sendCoupon;
    @Column(name="create_time")
    private Timestamp createTime;
    @Column(name="update_time")
    private Timestamp updateTime;
    /** 佣金计算方式; 0:商品实际支付金额*佣金比例；1：商品实际利润（实际支付金额-成本价）* 佣金比例*/
    @Column(name="strategy_type")
    private Byte strategyType;
    @Column(name="nav")
    private Integer   nav;
    /** 是否允许分销改价 0：否；1：是；*/
    @Column(name="change_price")
    private Byte changePrice;
    /** 改价有效期类型 0：指定时间；1：永久*/
    @Column(name="change_effective_type")
    private Byte changeEffectiveType;
    /** 有效时间*/
    @Column(name="effective_date")
    private Integer effectiveDate;
    /** 有效期单位 0：小时；1：天*/
    @Column(name="effective_unit")
    private Byte effectiveUnit;
    /**分页信息*/
    private int currentPage;
    private int pageRows;
}
