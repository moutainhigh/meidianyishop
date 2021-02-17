package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.wxapp.config.pledge.PledgeListParam;
import com.meidianyi.shop.service.pojo.wxapp.config.pledge.PledgeListVo;
import com.meidianyi.shop.service.pojo.wxapp.config.pledge.PledgeShowVo;
import com.meidianyi.shop.service.shop.config.PledgeConfigService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序-服务承诺
 *
 * @author liangchen 2019.10.30
 */
@RestController
@RequestMapping("/api/wxapp/config/pledge")
public class WxAppBaseConfigController extends WxAppBaseController {

  /**
   * 小程序-服务承诺列表
   *
   * @return JsonResult
   */
  @PostMapping("/list")
  public JsonResult getPledgeList(@RequestBody PledgeListParam param) {
    // 声明出参
    PledgeShowVo pledgeShowVo = new PledgeShowVo();
    // 得到当前配置开关信息
    String pledgeSwitch = shop().config.pledgeCfg.getPledgeConfig();
    pledgeShowVo.setPledgeSwitch(pledgeSwitch);
    // 若开关状态为0-关闭
    if (PledgeConfigService.V_PLEDGE_CLOSE.equals(pledgeSwitch)) {
      return success(pledgeShowVo);
    }
    // 否则为1-开启
    List<PledgeListVo> result = shop().shopBasicConfig.shopPledge.wxAppPledgeList(param);
    pledgeShowVo.setPledgeListVo(result);
    return success(pledgeShowVo);
  }
}
