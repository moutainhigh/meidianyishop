package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.anchor.AnchorPointsListParam;
import com.meidianyi.shop.service.pojo.shop.anchor.AnchorPointsParam;
import com.meidianyi.shop.service.shop.anchor.AnchorPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 锚点
 * @author 孔德成
 * @date 2020/8/31 11:02
 */
@RestController
public class AdminAnchorPointsController  extends AdminBaseController {

    @Autowired
    private AnchorPointsService anchorPointsService;
    /**
     * 锚点
     * @return
     */
    @PostMapping("/api/admin/anchor/points")
    public JsonResult anchorPoints(@RequestBody @Validated AnchorPointsParam param){
        saas.getShopApp(param.getShopId()).anchorPointsService.add(param);
        return success();
    }

    /**
     * 报表列表查询
     * @return lsit
     */
    @PostMapping("/api/admin/anchor/points/list")
    public JsonResult list(@RequestBody AnchorPointsListParam param){
        return success(anchorPointsService.list(param));
    }

    /**
     * 埋点报表
     * @param param
     * @return
     */
    @PostMapping("/api/admin/anchor/points/report")
    public JsonResult report(@RequestBody AnchorPointsListParam param){
        return success(anchorPointsService.countReport(param));
    }

    /**
     * 埋点报表
     * @param param
     * @return
     */
    @PostMapping("/api/admin/anchor/points/report/money")
    public JsonResult moneyReport(@RequestBody AnchorPointsListParam param){
        return success(anchorPointsService.moneyReport(param));
    }

    /**
     * 获取事件和参数
     * @return
     */
    @PostMapping("/api/admin/anchor/points/event/key/map")
    public JsonResult eventKeyMap(){
        return success(anchorPointsService.eventKeyMap());
    }

}
