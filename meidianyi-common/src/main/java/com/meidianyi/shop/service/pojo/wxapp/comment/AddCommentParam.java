package com.meidianyi.shop.service.pojo.wxapp.comment;

import lombok.Data;

/**
 * 用户添加评价的入参
 *
 * @author liangchen 2019.10.16
 */
@Data
public class AddCommentParam {
    /** order_goods rec_id */
    private Integer recId;
    /** 规格id */
    private Integer prdId;
  /** 商品id */
  private Integer goodsId;
  /** 用户id */
  private Integer userId;
  /** 订单编号 */
  private String orderSn;
  /** 评价星级 */
  private Byte commstar;
  /** 评价心得 */
  private String commNote;
  /** 晒单 */
  private String commImg;
  /** 匿名评价 0:不匿名 1:匿名 */
  private Byte anonymousflag;
  /** 评价有礼活动id */
  private Integer id;
  /** 评价有礼奖励类型 1积分 2优惠券 3余额 4幸运大抽奖 5自定义 */
  private Integer awardType;
  /** 评价有礼奖励内容 */
  private String award;
}
