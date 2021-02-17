package com.meidianyi.shop.service.pojo.shop.market.bargain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfigVo;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponView;
import com.meidianyi.shop.service.pojo.shop.member.tag.TagVo;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2019-09-04 18:04
 **/
@Data
public class BargainUpdateVo {
    private Integer    id;
    private String     bargainName;
    private Timestamp startTime;
    private Timestamp  endTime;
    private Integer    expectationNumber;
    private Double     bargainMin;
    private Double     bargainMax;

    private Byte       bargainType;
    private Byte       bargainMoneyType;
    private BigDecimal bargainFixedMoney;
    private BigDecimal bargainMinMoney;
    private BigDecimal bargainMaxMoney;
    private Byte       freeFreight;
    private Byte needBindMobile;
    private Integer initialSales;
    private Integer first;

    /**
     * 砍价商品
     */
    private List<BargainGoodsUpdateVo> bargainGoods;

    private String     mrkingVoucherId;
    private List<CouponView> mrkingVoucherList;
    private String     rewardCouponId;
    private List<CouponView> rewardCouponList;

    private String shareConfig;
    private PictorialShareConfigVo shopShareConfig;

    /**
     * 自定义活动说明
     */
    private String activityCopywriting;

    /**
     * 是否给发起砍价用户打标签，1是
     */
    private Byte launchTag;
    /**
     * 是否给发起砍价用户打标签标签列表
     */
    private List<TagVo> launchTagList;
    @JsonIgnore
    private String launchTagId;
    /**
     * 是否给参加活动的用户打标签，1是
     */
    private Byte attendTag;
    /**
     * 参加活动的用户打标签标签列表
     */
    private List<TagVo> attendTagList;
    @JsonIgnore
    private String attendTagId;
}
