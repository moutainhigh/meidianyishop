package com.meidianyi.shop.service.pojo.wxapp.config.pledge;

import lombok.Data;

/**
 * 小程序-服务承诺出参
 *
 * @author liangchen 2019.10.31
 */
@Data
public class PledgeListVo {
  /** 服务承诺id */
  private Integer id;
  /** 名称 */
  private String pledgeName;
  /** 图片url */
  private String pledgeLogo;
  /** 承诺内容 */
  private String pledgeContent;
    /** 优先级 */
    private Integer level;
}
