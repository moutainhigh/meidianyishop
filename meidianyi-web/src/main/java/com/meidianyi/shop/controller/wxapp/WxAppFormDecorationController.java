package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.market.form.FormDetailVo;
import com.meidianyi.shop.service.pojo.shop.market.form.pojo.FormInfoBo;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import com.meidianyi.shop.service.pojo.wxapp.market.form.FormGetParam;
import com.meidianyi.shop.service.pojo.wxapp.market.form.FormSubmitDataParam;
import com.meidianyi.shop.service.pojo.wxapp.market.form.FormSuccessParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 表单统计
 * @author 孔德成
 * @date 2020/3/13
 */
@RestController
@RequestMapping("/api/wxapp/form")
public class WxAppFormDecorationController extends WxAppBaseController{

    /**
     * 获取表单配置
     * @return
     */
    @PostMapping("/get")
    public JsonResult getInfo(@RequestBody @Validated FormGetParam param){
        Integer userId = wxAppAuth.user().getUserId();
        param.setUserId(userId);
        FormInfoBo formDecorationInfo = shop().formService.getFormDecorationInfo(param.getPageId(), param.getUserId(),getLang());
        return success(formDecorationInfo);
    }

    /**
     * 提交表单
     * @return
     */
    @PostMapping("/submit")
    public JsonResult submitFormData(@RequestBody @Validated FormSubmitDataParam param) throws MpException {
        WxAppSessionUser user = wxAppAuth.user();
        param.setUser(user);
        return success(shop().formService.submitFormDate(param,getLang()));
    }

    /**
     * 表单提交成功
     */
    @PostMapping("/success")
    public JsonResult submitSuccess(@RequestBody @Validated FormSuccessParam param){
        return success(shop().formService.submitSuccess(param));
    }
}
