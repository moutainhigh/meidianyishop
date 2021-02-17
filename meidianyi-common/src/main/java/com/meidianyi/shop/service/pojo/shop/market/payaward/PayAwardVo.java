package com.meidianyi.shop.service.pojo.shop.market.payaward;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponVo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 孔德成
 * @date 2019/8/13 13:49
 */
@Getter
@Setter
public class PayAwardVo {


    private Integer    id;
    private String     activityNames;
    /**
     * 时间类型 0 定时 1永久
     */
    private Byte timeType;
    private Timestamp  startTime;
    private Timestamp  endTime;
    private Timestamp  createTime;
    /**
     * 优先级
     */
    private Integer actFirst;
    /**
     *  商品范围类型
     */
    private Integer goodsAreaType;
    /**
     * 商品id
     */
    private String goodsIds;
    /**
     * 商品平台分类
     */
    private String goodsCatIds;
    /**
     * 商品商家分类
     */
    private String goodsSortIds;
    /**
     * 最少支付金额
     */
    private BigDecimal minPayMoney;
    /**
     * 每个用户参与次数
     */
    private Integer limitTimes;
    /**
     * 状态
     */
    private Byte status;
    /**
     * 奖励内容
     */
    private List<PayAwardContentBo> awardContentList;
    @JsonIgnore
    private String awardList;
}
