package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.medical.goods.param.MedicalGoodsPageListParam;
import com.meidianyi.shop.service.pojo.shop.medical.goods.vo.GoodsPageListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class WxAppMedicalGoodsController extends WxAppBaseController{

    /**
     * 药品分页查询
     * @param pageListParam 分页信息
     * @return
     */
    @PostMapping("/api/wxapp/medical/goods/page/list")
    public JsonResult getGoodsPageList(@RequestBody MedicalGoodsPageListParam pageListParam){
        PageResult<GoodsPageListVo> goodsPageList = shop().medicalGoodsService.getGoodsPageList(pageListParam);
        return success(goodsPageList);
    }
}
