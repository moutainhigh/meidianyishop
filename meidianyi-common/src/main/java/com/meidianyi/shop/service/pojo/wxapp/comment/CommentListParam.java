package com.meidianyi.shop.service.pojo.wxapp.comment;

import lombok.Data;

/**
 * 获取评价列表需要的入参
 *
 * @author liangchen 2019.10.15
 */
@Data
public class CommentListParam {
  /** 评论标识 0:未评论，1:已评论，2:已晒单 */
  private Byte commentFlag;
  /** 用户id */
  private Integer userId;
  /** 订单编号 */
  private String orderSn;
}
