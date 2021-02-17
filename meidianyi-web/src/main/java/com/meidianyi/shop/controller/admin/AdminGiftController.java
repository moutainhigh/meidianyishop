package com.meidianyi.shop.controller.admin;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.market.gift.GiftDetailListParam;
import com.meidianyi.shop.service.pojo.shop.market.gift.GiftListParam;
import com.meidianyi.shop.service.pojo.shop.market.gift.GiftParam;
import com.meidianyi.shop.service.pojo.shop.market.gift.LevelParam;

/**
 * 赠品
 *
 * @author 郑保乐
 */
@Validated
@RestController
@RequestMapping("/api/admin/market/gift")
public class AdminGiftController extends AdminBaseController {

    /**
     * 创建活动
     */
    @PostMapping("/add")
    public JsonResult addGift(@RequestBody @Valid GiftParam param) {
        shop().gift.addGift(param);
        return success();
    }

    /**
     * 编辑 - 查询明细
     */
    @PostMapping("/detail/{id}")
    public JsonResult getDetail(@PathVariable Integer id) {
        return success(shop().gift.getGiftDetail(id));
    }

    /**
     * 修改活动
     */
    @PostMapping("/update")
    public JsonResult updateGift(@RequestBody @Valid GiftParam param) {
        shop().gift.updateGift(param);
        return success();
    }

    /**
     * 停用活动
     */
    @PostMapping("/disable/{id}")
    public JsonResult disableGift(@PathVariable Integer id) {
        shop().gift.disableGift(id);
        return success();
    }

    /**
     * 启用活动
     */
    @PostMapping("/enable/{id}")
    public JsonResult enableGift(@PathVariable Integer id) {
        shop().gift.enableGift(id);
        return success();
    }

    /**
     * 删除活动
     */
    @PostMapping("/delete/{id}")
    public JsonResult deleteGift(@PathVariable Integer id) {
        shop().gift.deleteGift(id);
        return success();
    }

    /**
     * 列表查询
     */
    @PostMapping("/list")
    public JsonResult getPageList(@RequestBody GiftListParam param) {
        return success(shop().gift.getPageList(param));
    }

    /**
     * 修改优先级
     */
    @PostMapping("/level/update")
    public JsonResult updateLevel(@RequestBody @Valid LevelParam param) {
        shop().gift.updateLevel(param);
        return success();
    }

    /**
     * 赠品明细
     */
    @PostMapping("/gift_detail")
    public JsonResult giftDetail(@RequestBody @Valid GiftDetailListParam param) {
        return success(shop().gift.getGiftDetailPageList(param));
    }

    /**
     * 获取会员卡列表
     */
    @PostMapping("/member_card/list")
    public JsonResult getMemberCardList() {
        return success(shop().gift.getMemberCardList());
    }

    /**
     * 获取会员卡列表
     */
    @PostMapping("/tag/list")
    public JsonResult getTagList() {
        return success(shop().gift.getUserTagList());
    }

    /**
     * 获取商品规格明细
     */
    @PostMapping("/product/{giftId}/{productId}")
    public JsonResult getProductDetail(@PathVariable Integer giftId, @PathVariable Integer productId) {
        return success(shop().gift.getProductDetail(giftId, productId));
    }

    /**
     * 赠品叠加
     * 当买家满足多个活动的赠品条件时:1只赠送其中优先级最高的活动赠品;0赠送满足赠品条件的所有赠品
     */
    @PostMapping("/cfg/get")
    public JsonResult getCfg( ) {
        return success(shop().config.giftConfigService.getCfg());
    }

    /**
     * 赠品叠加
     * 当买家满足多个活动的赠品条件时:1只赠送其中优先级最高的活动赠品;2赠送满足赠品条件的所有赠品
     */
    @PostMapping("/cfg/set")
    public JsonResult setCfg(@RequestParam(value = "cfg") @NotNull Byte cfg) {
        shop().config.giftConfigService.setCfg(cfg);
        return success();
    }
}
