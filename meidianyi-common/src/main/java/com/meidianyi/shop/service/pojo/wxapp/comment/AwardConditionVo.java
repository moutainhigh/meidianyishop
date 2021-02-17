package com.meidianyi.shop.service.pojo.wxapp.comment;

import lombok.Data;

/**
 * 得奖所需满足条件的出参
 *
 * @author liangchen 2019.10.21
 */
@Data
public class AwardConditionVo {
  /** 评价类型 1评价即送 2 自定义 */
  private Byte commentType;
  /** 评价字数条件 */
  private Integer commentWords;
  /** 晒图 */
  private Byte hasPicNum;
  /** 五星好评 */
  private Byte hasFiveStars;
}
