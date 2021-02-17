package com.meidianyi.shop.service.pojo.wxapp.config.pledge;

import lombok.Data;

/**
 * 小程序-服务承诺入参
 *
 * @author liangchen 2019.10.31
 */
@Data
public class PledgeListParam {
  /** 商品id */
  private Integer goodsId;
  /** 种类id */
  private Integer catId;
  /** 品牌id */
  private Integer brandId;
}
