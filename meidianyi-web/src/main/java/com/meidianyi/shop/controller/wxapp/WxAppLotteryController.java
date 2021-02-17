package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.market.lottery.JoinLottery;
import com.meidianyi.shop.service.pojo.shop.market.lottery.JoinLotteryParam;
import com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryVo;
import com.meidianyi.shop.service.pojo.shop.market.lottery.record.LotteryRecordPageListVo;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import com.meidianyi.shop.service.pojo.wxapp.market.lottery.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 抽奖
 * @author 孔德成
 * @date 2020/1/16 15:43
 */
@RestController
@RequestMapping("/api/wxapp/lottery")
public class WxAppLotteryController extends WxAppBaseController{

    /**
     * 获取活动信息
     * @param param
     * @return
     */
    @PostMapping("/get")
    public JsonResult get(@RequestBody @Valid LotteryIdParam param) {
        WxAppSessionUser user = wxAppAuth.user();
        LotteryInfoVo lotteryInfoVo =new LotteryInfoVo();
        //活动信息
        LotteryVo lotteryVo = shop().lottery.getLotteryVo(param.getId());
        if (lotteryVo==null){
            return fail(JsonResultCode.CODE_PARAM_ERROR);
        }
        //用户抽奖情况
        LotteryUserTimeInfo userLotteryTimeInfo = shop().lottery.getUserLotteryInfo(user.getUserId(), param.getId());
        lotteryInfoVo.setLotteryInfo(lotteryVo);
        lotteryInfoVo.setLotteryUserTimeInfo(userLotteryTimeInfo);
        return success(lotteryInfoVo);
    }

    /**
     * 分享接口修改
     * @return
     */
    @PostMapping("/share")
    public JsonResult share(@RequestBody @Valid LotteryShareParam param){
        WxAppSessionUser user = wxAppAuth.user();
        shop().lottery.shareLottery(user.getUserId(),param.getLotteryId());
        return success();
    }

    /**
     * 参加抽奖活动
     * @param param
     * @return
     */
    @PostMapping("/join")
    public JsonResult joinLottery(@RequestBody @Valid JoinLotteryParam param){
        WxAppSessionUser user = wxAppAuth.user();
        param.setUserId(user.getUserId());
        JoinLottery joinLottery = shop().lottery.joinLottery(param);
        if (!joinLottery.getFlag()){
            JsonResult fail = fail(joinLottery.getResultMessage());
            joinLottery.setMsg(fail.getMessage().toString());
        }
        return success(joinLottery);
    }

    /**
     * 用户抽奖列表
     * @param param
     * @return
     */
    @PostMapping("/user/list")
    public JsonResult lotteryListByUser(@RequestBody @Valid LotteryListUserParam param){
        WxAppSessionUser user = wxAppAuth.user();
        param.setUserId(user.getUserId());
        PageResult<LotteryRecordPageListVo> lotteryRecordPageListVoPageResult = shop().lottery.lotteryListByUser(param);
        return success(lotteryRecordPageListVoPageResult);
    }
}
