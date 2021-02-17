package com.meidianyi.shop.service.pojo.wxapp.comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 评价列表出参
 *
 * @author liangchen 2019.10.15
 */
@Data
@NoArgsConstructor
public class CommentListVo {

  public CommentListVo(Integer goodsId) {
    this.goodsId = goodsId;
  }
  /** order_goods rec_id */
  private Integer recId;
  /** 商品id */
  private Integer goodsId;
  /** 商品名称 */
  private String goodsName;
  /** 商品图片 */
  private String goodsImg;
  /** 规格id */
  private Integer prdId;
  /** 规格描述 */
  private String prdDesc;
  /** 订单提交时间 */
  private Timestamp createTime;
  /** 订单编号 */
  private String orderSn;
  /** 评价评分 */
  private Byte commstar;
  /** 评价心得 */
  private String commNote;
  /** 评价晒单 */
  private String commImg;
  /** 商家回复内容 */
  private String content;
  /** 评价有礼活动id */
  private Integer id;
  /** 评价有礼奖励类型 1积分 2优惠券 3余额 4幸运大抽奖 5自定义 */
  private Integer awardType;
  /** 评价有礼奖励内容 */
  private String award;
  /** 是否已评价标识 0：待评价 1：已评价 */
  private Byte commentFlag;
  /** 评价类型 1评价即送 2 自定义 */
  private Byte commentType;
  /** 评价字数条件 */
  private Integer commentWords;
  /** 晒图 */
  private Byte hasPicNum;
  /** 五星好评 */
  private Byte hasFiveStars;
}
