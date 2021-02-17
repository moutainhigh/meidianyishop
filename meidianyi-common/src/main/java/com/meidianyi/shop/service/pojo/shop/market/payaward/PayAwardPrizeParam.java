package com.meidianyi.shop.service.pojo.shop.market.payaward;

import com.meidianyi.shop.service.pojo.shop.coupon.CouponView;
import com.meidianyi.shop.service.pojo.shop.goods.spec.ProductSmallInfoVo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 孔德成
 * @date 2019/12/20 14:27
 */
@Getter
@Setter
@ToString
public class PayAwardPrizeParam {
    /**
     * id
     */
    private Integer id;
    /**
     * 奖励类型
     */
    private Byte giftType;
    /**
     * 优惠卷
     */
    private List<PayAwardCouponBo> couponList;

    /**
     * 优惠卷
     */
    private List<CouponView> couponView;
    /**
     * 优惠卷ids
     */
    private List<Integer> couponIds;
    /**
     * 积分
     */
    private Integer scoreNumber;
    /**
     * 账户
     */
    private BigDecimal accountNumber;
    /**
     * 抽奖活动id
     */
    private Integer lotteryId;
    /**
     * 规格id
     */
    private Integer productId;
    /**
     * 规格信息
     */
    private ProductSmallInfoVo product;
    /**
     * 赠品有效期
     */
    private Integer keepDays;

    /**
     * 自定义活动图片
     */
    private String customImage;
    /**
     * 自定义活动链接
     */
    private String customLink;
    /**
     * 奖品份数
     */
    private Integer awardNumber;
    /**
     * 发送份数
     */
    private Integer sendNum=0;
}
