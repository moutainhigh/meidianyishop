package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.config.rebate.RebateConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangpengcheng
 * @date 2020/8/24
 **/
@RestController
@RequestMapping("/api/admin")
public class AdminRebateController extends AdminBaseController{

    /**
     * 获取返利配置
     * @return
     */
    @PostMapping("/rebate/get")
    public JsonResult getRebateConfig(){
        RebateConfig rebateConfig=shop().config.rebateConfigService.getRebateConfig();
        return success(rebateConfig);
    }
    /**
     * 获取返利配置
     * @return
     */
    @PostMapping("/rebate/set")
    public JsonResult setRebateConfig(@RequestBody RebateConfig rebateConfig){
        int result=shop().config.rebateConfigService.setRebateConfig(rebateConfig);
        return success(result);
    }
}
