package com.meidianyi.shop.controller.admin;

import java.util.List;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.validator.ValidList;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.service.pojo.shop.config.center.UserCenterConfigParam;

import javax.validation.Valid;

/**
 *
 * 个人中心配置
 * @author 孔德成
 * @date 2019/7/11 11:25
 */
@RestController
@RequestMapping("/api")
public class AdminUserCenterConfigController extends AdminBaseController{


    /**
     * 个人中心获取配置
     *
     * @return
     */
    @GetMapping("/admin/user/center/config/get")
    public JsonResult getUserCenterConfig(){
       return success(shop().config.userCenterConfigService.getkUserCenterConfig());
    }

    /**
     * 设置个人中心配置
     *
     * @param param
     * @return
     */
    @PostMapping("/admin/user/center/config/update")
    public JsonResult updateUserCenterConfig(@RequestBody @Validated ValidList<UserCenterConfigParam> param){
        shop().config.userCenterConfigService.updateUserCenterConfig(param);
        return success();
    }

}
