package com.meidianyi.shop.service.pojo.shop.coupon.give;

import lombok.Data;

/**
 * 定向发券明细
 *
 * @author liangchen
 * @date 2019年7月30日
 */
@Data
public class CouponGiveDetailParam {
    /**优惠券id*/
    private Integer couponId;
  /** 活动Id */
  private Integer accessId;
  /** 手机号 */
  private String mobile;
  /** 用户昵称 */
  private String username;
  /** 是否使用 1 未使用 2 已使用 3 已过期 4已废除 */
  private Integer isUsed = 0;

  /** 分页信息 */
  private Integer currentPage = 1 ;

  private Integer pageRows =20 ;
}
