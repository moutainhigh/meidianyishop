package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.config.trade.third.*;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 第三方配置
 * @author 孔德成
 * @date 2020/5/18
 */
@RestController
@RequestMapping("/api/admin/config/third/auth")
public class AdminConfigThirdController  extends AdminBaseController {

    /**
     * 获取erp/pos/crm配置信息
     * @return
     */
    @PostMapping("/get")
    public JsonResult getThirdAuthInfo(@RequestBody @Valid ThirdInfoParam param){
        return success(shop().thirdAuthService.getThirdAuthInfo(param));
    }

    /**
     * 推送配置
     * @param param
     * @return
     */
    @PostMapping("/push/set")
    public JsonResult setPush(@RequestBody @Valid ThirdErpPushParam param){
        shop().thirdAuthService.setPush(param);
        return success();
    }

    /**
     * 授权
     * @return
     */
    @PostMapping("/enable")
    public JsonResult authorize(@RequestBody @Validated ThirdAuthorizeParam param){
        int authorize = shop().thirdAuthService.authorize(param);
        if (authorize>0){
            return success();
        }
        return fail();
    }

    /**
     * 重置sessionKey
     * @return
     */
    @PostMapping("/resetsessionkey")
    public JsonResult resetSessionKey(@RequestBody @Valid ThirdAppAuthIdParam param){
        int i = shop().thirdAuthService.resetSessionKey(param.getId());
        if (i==1) {
            return success();
        } else {
            return fail();
        }
    }

    /**
     * 保存appkey
     * @return
     */
    @PostMapping("/appkey/save")
    public JsonResult saveAppKey(@RequestBody @Valid ThirdAppKeyParam param){
        int i = shop().thirdAuthService.saveAppKey(param);
        if (i>0) {
            return success();
        } else {
            return fail();
        }
    }

    /**
     * 切换erp版本
     * @return
     */
    @PostMapping("/erp/product")
    public JsonResult saveProduct(@RequestBody @Valid ThirdSwitchProductParam param){
        int i = shop().thirdAuthService.switchProduct(param);
        if (i>0){
            return success();
        }
        return fail();
    }

    /**
     * pos同步
     * @return
     */
    @PostMapping("/pos/sync")
    public JsonResult syncStoreGoods(ThirdPosSyncParam param ){
        shop().thirdAuthService.posSyncStoreGoods(param);
        return success();
    }

}
