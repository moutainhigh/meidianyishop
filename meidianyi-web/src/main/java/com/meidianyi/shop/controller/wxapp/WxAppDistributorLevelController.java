package com.meidianyi.shop.controller.wxapp;

/**
 * @author changle
 * @date 2020/8/17 10:33 上午
 */

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.wxapp.distribution.distributorlevel.DistributorLevelDetailParam;
import com.meidianyi.shop.service.pojo.wxapp.distribution.distributorlevel.DistributorLevelDetailVo;
import com.meidianyi.shop.service.pojo.wxapp.distribution.distributorlevel.DistributorLevelRecordParam;
import com.meidianyi.shop.service.pojo.wxapp.distribution.distributorlevel.DistributorLevelRecordVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wxapp/distributor/level/")
public class WxAppDistributorLevelController extends WxAppBaseController {
    @PostMapping("detail")
    public JsonResult distributorLevelDetail(@RequestBody DistributorLevelDetailParam param){
        DistributorLevelDetailVo distributorLevelDetail = shop().mpDisLevel.getDistributorLevelDetail(param);
        return this.success(distributorLevelDetail);
    }

    /**
     * 分销员等级升降记录
     * @param param
     * @return
     */
    @PostMapping("record")
    public JsonResult distributorLevelRecord(@RequestBody DistributorLevelRecordParam param){
        PageResult<DistributorLevelRecordVo> disLevelRecord = shop().mpDisLevel.distributorLevelRecord(param);
        return this.success(disLevelRecord);
    }
}
