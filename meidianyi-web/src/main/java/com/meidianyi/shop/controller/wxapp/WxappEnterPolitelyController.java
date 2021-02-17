package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.wxapp.market.enterpolitely.EnterPolitelyParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Wxapp enter politely controller.
 *
 * @author liufei
 * @date 12 /23/19 开屏有礼
 */
@RestController
@RequestMapping("/api/wxapp/enterpolitely")
public class WxappEnterPolitelyController extends WxAppBaseController {

    /**
     * Gets the gift.
     *
     * @return the the gift
     */
    @PostMapping("/index")
    public JsonResult getTheGift(@RequestBody @Validated EnterPolitelyParam param) {
        return this.success(shop().enterPolitelyService.enterPolitely(param.getUserId()));
    }
}
