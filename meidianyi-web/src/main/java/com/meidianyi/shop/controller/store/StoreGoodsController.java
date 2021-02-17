package com.meidianyi.shop.controller.store;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.store.goods.StoreGoodsListQueryParam;
import com.meidianyi.shop.service.pojo.shop.store.goods.StoreGoodsListQueryVo;
import com.meidianyi.shop.service.shop.store.store.StoreGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李晓冰
 * @date 2020年08月31日
 */
@RestController
public class StoreGoodsController extends StoreBaseController{
    @Autowired
    private StoreGoodsService storeGoodsService;

    @PostMapping("/api/store/goods/list")
    public JsonResult getGoodsPageList(@RequestBody StoreGoodsListQueryParam param){
        param.setLimitedStoreIds(storeAuth.user().getStoreIds());
        PageResult<StoreGoodsListQueryVo> goodsPageList = storeGoodsService.getGoodsPageList(param);
        return success(goodsPageList);
    }
}
