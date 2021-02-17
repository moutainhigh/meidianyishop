package com.meidianyi.shop.service.pojo.shop.market.bargain;

import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfig;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 王兵兵
 *
 * 2019年7月25日
 */
@Data
public class BargainUpdateParam {

    /**
     * 活动的主键
     */
    @NotNull
    @Min(1)
    private Integer id;

    /**
     * 活动类型：0砍到指定金额结算，1砍到任意金额结算
     */
    private Byte bargainType;

    /**
     * 状态：1可用，0停用
     */
    @Min(0)
    @Max(1)
    private Byte status;

    private String bargainName;

    private Timestamp  endTime;
    /**
     * 优先级
     */
    private Integer first;

    /**
     *  活动商品
     */
    private List<BargainGoodsUpdateParam> bargainGoods;

    /**
     * 商品首次砍价可砍价比例区间 百分比
     */
    private Double     bargainMin;
    private Double     bargainMax;

    private String     mrkingVoucherId;
    private String     rewardCouponId;

    /**
     *  任意金额结算模式的单次帮砍金额模式：0固定金额，1区间随机金额
     */
    private Byte       bargainMoneyType;
    private BigDecimal bargainFixedMoney;
    private BigDecimal bargainMinMoney;
    private BigDecimal bargainMaxMoney;
    private Byte freeFreight;
    private Byte needBindMobile;
    /**
     * 初始销量(初始砍价人次)
     */
    private Integer initialSales;


    private PictorialShareConfig shareConfig;

    /**
     * 自定义活动说明
     */
    private String activityCopywriting;
    /**
     * 是否给发起砍价用户打标签，1是
     */
    private Byte launchTag;
    /**
     * 发起砍价活动用户打标签id列表
     */
    private List<Integer> launchTagId;
    /**
     * 是否参与砍价用户打标签，1是
     */
    private Byte attendTag;
    /**
     * 参与砍价活动用户打标签id列表
     */
    private List<Integer> attendTagId;
}
