package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.wxapp.share.ShareRecordParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序-活动商品分享操作记录
 * @author 李晓冰
 * @date 2019年12月26日
 */
@RestController
public class WxAppShareRecordController extends WxAppBaseController{

    /**
     * 活动商品分享操作记录表
     * @return {@link JsonResult}
     */
    @PostMapping("/api/wxapp/share/add")
    public JsonResult addShareRecord(@RequestBody ShareRecordParam param){
        Integer userId = wxAppAuth.user().getUserId();
        param.setUserId(userId);
        shop().goodsActivityShareRecord.addShareRecord(param);
        return success();
    }
}
