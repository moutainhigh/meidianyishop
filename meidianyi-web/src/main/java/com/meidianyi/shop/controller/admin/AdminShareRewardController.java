package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.config.pledge.group.UpdateGroup;
import com.meidianyi.shop.service.pojo.shop.market.sharereward.ShareReceiveDetailParam;
import com.meidianyi.shop.service.pojo.shop.market.sharereward.ShareRewardAddParam;
import com.meidianyi.shop.service.pojo.shop.market.sharereward.ShareRewardShowParam;
import com.meidianyi.shop.service.pojo.shop.market.sharereward.ShareRewardStatusParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author liufei
 * @date 2019/8/19
 * @description
 */
@RestController
@RequestMapping("/api/admin/market/sharereward")
public class AdminShareRewardController extends AdminBaseController {
    /**
     * 分享有礼分页条件查询
     */
    @PostMapping("/selectbypage")
    public JsonResult selectByPage(@RequestBody @Validated ShareRewardShowParam param) {
        return success(new HashMap<String, Object>(1) {
			private static final long serialVersionUID = -770352000451470300L;
			{
                put("dailyShareAward", shop().shareRewardService.getDailyShareAwardValue());
                put("pageResult", shop().shareRewardService.selectByPage(param));
            }
        });
    }

    /**
     * 添加分享有礼活动
     */
    @PostMapping("/addsharereward")
    public JsonResult addShareReward(@RequestBody @Validated ShareRewardAddParam param) {
        shop().shareRewardService.addShareReward(param);
        return success();
    }

    /**
     * 获取分享有礼活动详情
     */
    @GetMapping("/getsharerewardinfo/{shareId}")
    public JsonResult getShareRewardInfo(@PathVariable Integer shareId) {
        return success(shop().shareRewardService.getShareRewardInfo(shareId));
    }

    /**
     * 更新分享有礼活动
     */
    @PostMapping("/updatesharereward")
    public JsonResult updateShareReward(@RequestBody @Validated(UpdateGroup.class) ShareRewardAddParam param) {
        shop().shareRewardService.updateShareReward(param);
        return success();
    }

    /**
     * 停用/启用/删除
     */
    @PostMapping("/changeactivity")
    public JsonResult changeActivity(@RequestBody @Validated ShareRewardStatusParam param) {
        shop().shareRewardService.changeActivity(param);
        return success();
    }

    /**
     * 分享有礼活动奖励领取明细查询
     */
    @PostMapping("/sharereceivedetail")
    public JsonResult shareReceiveDetail(@RequestBody @Validated ShareReceiveDetailParam param) {
        return success(shop().shareRewardService.shareReceiveDetail(param));
    }

    /**
     * 更新每日用户可分享次数上限参数
     */
    @GetMapping("/updatedailyshareaward/{limit}")
    public JsonResult updateDailyShareAward(@PathVariable Integer limit) {
        shop().shareRewardService.updateDailyShareAward(limit);
        return success();
    }
}
