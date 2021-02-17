package com.meidianyi.shop.controller.store;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.pojo.shop.auth.StoreLoginParam;
import com.meidianyi.shop.service.pojo.shop.auth.StoreTokenAuthInfo;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author chenjie
 * @date 2020年08月26日
 */
@RestController
@RequestMapping("/api")
public class StoreLoginController extends StoreBaseController {
    @PostMapping(value = "/store/login")
    public JsonResult login(@RequestBody @Valid StoreLoginParam param) {
        StoreTokenAuthInfo info = storeAuth.login(param);
        return this.success(info);
    }


    @GetMapping(value = "/store/logout")
    public JsonResult logout() {
        storeAuth.logout();
        return success(JsonResultCode.CODE_SUCCESS);
    }

    /**
     * 判断用户是否在线，首页右上角是否显示用户信息用
     * @return
     */
    @GetMapping(value = "/store/login/isLogin")
    public JsonResult checkToken() {
        StoreTokenAuthInfo user = storeAuth.user();
        if(user!=null) {
            return success();
        }
        return fail();
    }
}
