package com.meidianyi.shop.service.pojo.wxapp.comment;

import lombok.Data;

/**
 * 评价列表出参
 *
 * @author liangchen 2019.10.15
 */
@Data
public class TriggerConditionVo {
  /** 评价有礼活动id */
  private Integer id;
  /** 触发条件 1全部商品 2指定商品 3 实际评论比较少的商品 */
  private Integer goodsType;
  /** 对应商品id */
  private String goodsIds;
  /** 评论数 */
  private Integer commentNum;
  /** 首次评价商品 */
  private Byte firstCommentGoods;
}
