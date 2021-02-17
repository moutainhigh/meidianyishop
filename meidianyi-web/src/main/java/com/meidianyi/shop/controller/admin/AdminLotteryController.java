package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.market.MarketSourceUserListParam;
import com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryByIdParam;
import com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryPageListParam;
import com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryPageListVo;
import com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryParam;
import com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryVo;
import com.meidianyi.shop.service.pojo.shop.market.lottery.record.LotteryRecordPageListParam;
import com.meidianyi.shop.service.pojo.shop.market.lottery.record.LotteryRecordPageListVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author 孔德成
 * @date 2019/8/5 14:16
 */
@RestController
@RequestMapping("/api/admin/market/lottery")
public class AdminLotteryController extends AdminBaseController {


    /**
     *  抽奖活动列表
     * @return JsonResult
     */
    @PostMapping("/list")
    public JsonResult getLotteryList(@RequestBody @Valid LotteryPageListParam param){
        PageResult<LotteryPageListVo> result = shop().lottery.getLotteryList(param);
        return success(result);
    }

    /**
     * 获取可用的抽奖活动列表
     * @return
     */
    @PostMapping("/usablelist")
    public JsonResult getLotteryUsableAllList(){
        LotteryPageListParam param = new LotteryPageListParam();
        //进行中,未开始
        param.setState(BaseConstant.NAVBAR_TYPE_AVAILABLE);
        param.setPageRows(Integer.MAX_VALUE);
        PageResult<LotteryPageListVo> result = shop().lottery.getLotteryList(param);
        return success(result);
    }


    /**
     * 新增抽奖活动
     * @param param param
     * @return json
     */
    @PostMapping("/add")
    public JsonResult addLottery(@RequestBody @Valid  LotteryParam param){
        Integer integer = shop().lottery.addLottery(param);
        if (integer<1){
            return fail();
        }
        return success();
    }

    /**
     * 更新抽奖活动
     * @param param param
     * @return json
     */
    @PostMapping("/update")
    public JsonResult updateLottery(@RequestBody @Valid LotteryParam param){
        Integer flag= shop().lottery.updateLottery(param);
        if (flag<1){
            return fail();
        }
        return success();
    }

    /**
     * 分享
     * @param param
     * @return
     */
    @PostMapping("/share")
    public JsonResult share(@RequestBody @Valid LotteryByIdParam param){
        return success(shop().lottery.getMpQrCode(param));
    }

    /**
     * 改变状态
     * @param param param
     * @return json
     */
    @PostMapping("/change")
    public JsonResult closeAndRestartById(@RequestBody @Valid LotteryByIdParam param){
        Integer flag = shop().lottery.closeAndRestartById(param.getId());
        if (flag<1){
            return fail();
        }
        return success();
    }

    @PostMapping("/delete")
    public JsonResult deleteLottery(@RequestBody @Valid LotteryByIdParam param){
        Integer flag = shop().lottery.deleteLottery(param.getId());
        if (flag<1){
            return fail();
        }
        return success();
    }

    /**
     * 获取单个抽象活动信息
     * @param param param
     * @return json
     */
    @PostMapping("/get")
    public JsonResult getLotteryById(@RequestBody @Valid LotteryByIdParam param){
        LotteryVo lotteryVo = shop().lottery.getLotteryVo(param.getId());
        if (lotteryVo==null){
            return fail(JsonResultCode.CODE_PARAM_ERROR);
        }
        return success(lotteryVo);
    }

    /**
     * 查询抽奖活动的记录
     * @param param param
     * @return json
     */
    @PostMapping("/record/list")
    public JsonResult getLotteryRecordList(@RequestBody @Valid LotteryRecordPageListParam param){
        PageResult<LotteryRecordPageListVo> result = shop().lottery.getLotteryRecordList(param);
        return success(result);
    }

    /**
     *  查询抽奖用户列表
     * @return json
     */
    @PostMapping("/user/list")
    public JsonResult getLotteryUserList(@RequestBody @Valid MarketSourceUserListParam param){
        return success(shop().lottery.getLotteryUserList(param));
    }

}
