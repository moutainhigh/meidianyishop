package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.FreeShippingRecord;
import com.meidianyi.shop.db.shop.tables.records.FreeShippingRuleRecord;
import com.meidianyi.shop.service.pojo.shop.config.pledge.group.UpdateGroup;
import com.meidianyi.shop.service.pojo.shop.market.freeshipping.*;
import org.jooq.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * 满包邮活动
 *
 * @author 孔德成
 * @date 2019/7/29 15:59
 */
@RestController
@RequestMapping("/api")
public class AdminFreeShippingController extends AdminBaseController {


    /**
     * 列表
     *
     * @return
     */
    @PostMapping("/admin/market/free/shipping/list")
    public JsonResult freeShippingList(@RequestBody @Valid FreeShipQueryParam param) {
        PageResult<FreeShippingVo> freeShippingList = shop().freeShipping.getFreeShippingList(param);
        return success(freeShippingList);
    }

    /**
     * 获取满包邮详情
     *
     * @param param
     * @return
     */
    @PostMapping("/admin/market/free/shipping/get")
    public JsonResult getFreeShipping(@RequestBody @Valid  FreeShippingChangeParam param) {
        if (param.getId() == null) {
            return fail();
        }
        FreeShippingRecord freeShipping = shop().freeShipping.getFreeShippingById(param.getId());
        Result<FreeShippingRuleRecord> freeShippingRule = shop().freeShipping.ruleService.getFreeShippingRule(param.getId());
        FreeShippingVo freeShippingVo = freeShipping.into(FreeShippingVo.class);
        freeShippingVo.setRuleList(freeShippingRule.into(FreeShippingRuleVo.class));
        return success(freeShippingVo);
    }


    /**
     * 添加
     *
     * @param param
     * @return
     */
    @PostMapping("/admin/market/free/shipping/add")
    public JsonResult addFreeShipping(@RequestBody @Valid FreeShippingParam param) {
        shop().freeShipping.addFreeShipping(param);
        return success();
    }

    /**
     * 修改
     *
     * @return
     */
    @PostMapping("/admin/market/free/shipping/update")
    public JsonResult updateFreeShipping(@RequestBody @Validated(UpdateGroup.class) FreeShippingParam param) {
        if (param.getId() == null) {
            return fail();
        }
        shop().freeShipping.updateFreeShipping(param);
        return success();
    }

    @GetMapping("/admin/market/free/shipping/share/{ruleId}")
    public JsonResult shareFreeShipping(@PathVariable @NotEmpty Integer ruleId){
        return success(shop().freeShipping.shareFreeShipping(ruleId));
    }

    /**
     * 改变状态
     *
     * @param param
     * @return
     */
    @PostMapping("/admin/market/free/shipping/status/change")
    public JsonResult changeStatus(@RequestBody @Valid FreeShippingChangeParam param) {
        if (param.getId() == null) {
            return fail();
        }
        shop().freeShipping.changeStatus(param.getId());
        return success();
    }


    /**
     * 删除
     *
     * @param param
     * @return
     */
    @PostMapping("/admin/market/free/shipping/delete")
    public JsonResult deleteFreeShipping(@RequestBody FreeShippingChangeParam param) {
        if (param.getId() == null) {
            return fail();
        }
        shop().freeShipping.deleteFreeShipping(param.getId());
        return success();
    }

}
