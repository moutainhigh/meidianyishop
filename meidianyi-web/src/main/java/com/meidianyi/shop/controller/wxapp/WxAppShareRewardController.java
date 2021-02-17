package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.wxapp.market.sharereward.ShareParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Wx app share reward controller.
 *
 * @author liufei
 * @date 1 /9/20
 */
@RestController
public class WxAppShareRewardController extends WxAppBaseController {

    /**
     * Share detail json result.分享信息详情（商品详情页点击分享后弹出的分享详情信息）
     *
     * @param param the param
     * @return the json result
     */
    @PostMapping("/api/wxapp/shareaward/goods/sharedetail")
    public JsonResult shareDetail(@RequestBody @Validated ShareParam param) {
        return success(shop().wxShareReward.getShareInfo(param.getUserId(), param.getGoodsId(), param.getActivityId()));
    }

    /**
     * Share json result.分享没有礼
     *
     * @param param the param
     * @return the json result
     */
    @PostMapping("/api/wxapp/shareaward/goods/share")
    public JsonResult share(@RequestBody @Validated ShareParam param) {
        shop().wxShareReward.shareRecord(param);
        return success();
    }

    /**
     * Share award json result.分享有礼
     *
     * @param param the param
     * @return the json result
     */
    @PostMapping("/api/wxapp/shareaward/goods/shareaward")
    public JsonResult shareAward(@RequestBody @Validated ShareParam param) {
        shop().wxShareReward.shareAward(param);
        return success();
    }

}
