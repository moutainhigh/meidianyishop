package com.meidianyi.shop.service.pojo.wxapp.config.pledge;

import lombok.Data;

import java.util.List;

/**
 * 小程序-服务承诺最终展示参数
 *
 * @author liangchen 2019.10.31
 */
@Data
public class PledgeShowVo {
  /** 服务配置总开关 0：关闭-不显示 1：开启-显示 */
  private String pledgeSwitch;
  /** 服务承诺列表信息 */
  private List<PledgeListVo> pledgeListVo;
}
