package com.meidianyi.shop.service.pojo.shop.coupon.give;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
/**
 * 发放优惠券
 *
 * @author liangchen
 * @date 2019年7月31日
 */
@Data
public class CouponGiveGrantParam {
  /** 活动名称 */
  private String actName;

  /** json字符串 */
  CouponGiveGrantInfoParam couponGiveGrantInfoParams;

  /** 活动人群 */
  private String cardId;

  private String tagId;
  private String user;
  private Integer havePay;
  private Integer noPay;
  private Integer maxCount;
  private Integer minCount;
  private BigDecimal maxAvePrice;
  private BigDecimal minAvePrice;

  /** 发送时间 */
  private Byte sendAction;

  private Timestamp startTime;
}
