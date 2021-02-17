package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.shop.tables.records.MpOfficialAccountUserRecord;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangpengcheng
 * @date 2020/9/25
 **/
@RestController
@RequestMapping("/api/wxapp/public")
public class WxAppWechatApiController extends WxAppBaseController{
    /**
     * 获取绑定店铺的二维码
     * @return
     */
    @PostMapping("/service/bind/getOfficialQrCode")
    public JsonResult generateThirdPartCode() {
        WxAppSessionUser wxAppSessionUser=wxAppAuth.user();
        MpAuthShopRecord wxapp = saas.shop.mp.getAuthShopByShopId(wxAppSessionUser.getShopId());
        try {
            String generateThirdPartCode = saas.shop.officeAccount.generateThirdPartCodeForWx(wxAppSessionUser,wxapp.getLinkOfficialAppId());
            return success(generateThirdPartCode);
        } catch (WxErrorException e) {
            logger().debug(e.getMessage(),e);
        }
        return fail();

    }
    @PostMapping("/service/bind/getBindStatus")
    public JsonResult getBindStatus(){
        WxAppSessionUser wxAppSessionUser=wxAppAuth.user();
        MpOfficialAccountUserRecord officialUser = saas.getShopApp(wxAppSessionUser.getShopId()).officialAccountUser.getAccountUserByUserId(wxAppSessionUser.getUserId());
        if(officialUser==null){
            return success(0);
        }
        return success(1);
    }
}
