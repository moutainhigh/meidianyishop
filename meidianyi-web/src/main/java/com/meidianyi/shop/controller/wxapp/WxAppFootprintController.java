package com.meidianyi.shop.controller.wxapp;


import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.wxapp.footprint.FootprintListParam;
import com.meidianyi.shop.service.pojo.wxapp.footprint.FootprintListVo;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  足迹
 * @author 孔德成
 * @date 2019/11/4 10:52
 */
@RestController
@RequestMapping("/api/wxapp/footprint")
@Slf4j
public class WxAppFootprintController extends WxAppBaseController {




    /**
     * 订单列表
     * @return
     */
    @PostMapping("/list")
    public JsonResult getFootprintList(@RequestBody FootprintListParam param){
        WxAppSessionUser user = wxAppAuth.user();
        if (user!=null){
           param.setUserId(user.getUserId());
        }else {
            log.info("user = null ");
        }
        FootprintListVo footprintPage = shop().footPrintService.getFootprintPage(param.getUserId(), param.getKeyword(), param.getCurrentPage(), param.getPageRows());
        return  success(footprintPage);
    }

}
