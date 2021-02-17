package com.meidianyi.shop.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.market.givegift.GiveGiftIdParam;
import com.meidianyi.shop.service.pojo.shop.market.givegift.GiveGiftListParam;
import com.meidianyi.shop.service.pojo.shop.market.givegift.GiveGiftListVo;
import com.meidianyi.shop.service.pojo.shop.market.givegift.GiveGiftParam;
import com.meidianyi.shop.service.pojo.shop.market.givegift.GiveGiftVo;
import com.meidianyi.shop.service.pojo.shop.market.givegift.receive.GiveGiftReceiveListParam;
import com.meidianyi.shop.service.pojo.shop.market.givegift.receive.GiveGiftReceiveListVo;
import com.meidianyi.shop.service.pojo.shop.market.givegift.record.GiveGiftRecordListParam;
import com.meidianyi.shop.service.pojo.shop.market.givegift.record.GiveGiftRecordListVo;

import javax.validation.Valid;

/**
 * 我要送礼
 *
 * @author 孔德成
 * @date 2019/8/15 16:56
 */
@RestController
@RequestMapping("/api/admin/marker/givegift")
@Slf4j
public class AdminGiveGiftController extends AdminBaseController {


    /**
     * 我要送礼列表
     * @return
     */
    @PostMapping("/list")
    public JsonResult getGiveGiftList(@RequestBody @Validated GiveGiftListParam param) {
        PageResult<GiveGiftListVo> giveGiftList = shop().giveGift.getGiveGiftList(param);
        return success(giveGiftList);
    }

    /**
     * 获取当个活动详情
     * @param param id
     * @return json
     */
    @PostMapping("/get")
    public JsonResult getGiveGiftById(@RequestBody @Valid GiveGiftIdParam param){
        GiveGiftVo giveGiftVo = shop().giveGift.getGiveGiftById(param.getId()).into(GiveGiftVo.class);
        return success(giveGiftVo);
    }

        /**
         * 添加活动
         *
         * @param param 参数
         * @return 成功或失败
         */
        @PostMapping("/add")
        public JsonResult addGiveGift(@RequestBody @Valid GiveGiftParam param) {
            Integer flag = shop().giveGift.addGiveGift(param);
            if (flag<=0){
                return fail();
            }
            return success();
        }

    /**
     * 更新活动
     *
     * @param param 参数
     * @return 成功或失败
     */
    @PostMapping("/update")
    public JsonResult updateGiveGift(@RequestBody @Valid GiveGiftParam param) {
        Integer flag = shop().giveGift.updateGiveGift(param);
        if (flag>0){
            return success();
        }
        return fail();
    }

    /**
     * 改变状态
     * @param param id
     * @return 成功火失败
     */
    @PostMapping("/change/status")
    public JsonResult changeGiveGift(@RequestBody @Valid GiveGiftIdParam param) {
        int flag = shop().giveGift.changeGiveGift(param.getId());
        if (flag>0){
            return success();
        }
        return fail();
    }

    /**
     * 删除活动
     *
     * @param param id
     * @return 成功或失败
     */
    @PostMapping("/delete")
    public JsonResult deleteGiveGift(@RequestBody @Valid GiveGiftIdParam param) {
        int flag = shop().giveGift.deleteGiveGift(param.getId());
        if (flag>0){
            return success();
        }
        return fail();
    }


    /**
     * 送礼明细列表
     * @return 返回 page
     */
    @PostMapping("/send/list")
    public JsonResult giveGiftRecordList(@RequestBody @Valid GiveGiftRecordListParam param) {
        PageResult<GiveGiftRecordListVo> giveGiftRecordListVoPageResult = shop().giveGift.giveGiftRecordList(param);
        return success(giveGiftRecordListVoPageResult);
    }

    /**
     * 收里明细
     * @return page对象
     */
    @PostMapping("/receive/list")
    public JsonResult giveGiftReceiveList(@RequestBody @Valid GiveGiftReceiveListParam param) {
        PageResult<GiveGiftReceiveListVo> giveGiftReceiveListVoPageResult = shop().giveGift.giveGiftReceiveList(param);
        return success(giveGiftReceiveListVoPageResult);
    }


}
