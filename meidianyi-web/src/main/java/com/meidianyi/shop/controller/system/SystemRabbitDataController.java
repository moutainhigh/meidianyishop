package com.meidianyi.shop.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.saas.SaasApplication;

/**
 * @author luguangyao
 */
@RestController
public class SystemRabbitDataController extends SystemBaseController {


    @Autowired
    SaasApplication saasApplication;

    @PostMapping("/system/store_management/detail")
    public JsonResult getRabbitDetail(){
        return success(saasApplication.rabbitDataService.getDetails());
    }
}
