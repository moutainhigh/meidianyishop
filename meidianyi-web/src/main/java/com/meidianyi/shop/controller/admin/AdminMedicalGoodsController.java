package com.meidianyi.shop.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsExternalDo;
import com.meidianyi.shop.service.pojo.shop.medical.goods.entity.GoodsEntity;
import com.meidianyi.shop.service.pojo.shop.medical.goods.param.*;
import com.meidianyi.shop.service.pojo.shop.medical.goods.vo.GoodsDetailVo;
import com.meidianyi.shop.service.pojo.shop.medical.goods.vo.GoodsPageListVo;
import com.meidianyi.shop.service.pojo.shop.order.goods.store.*;
import com.meidianyi.shop.service.shop.order.goods.OrderStoreSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2020年07月02日
 */
@RestController
@Slf4j
public class AdminMedicalGoodsController extends AdminBaseController {

    /**
     * 药品新增
     * @param goodsEntity
     * @return
     */
    @PostMapping("/api/admin/medical/goods/insert")
    public JsonResult insert(@RequestBody GoodsEntity goodsEntity) {

        //判断商品名称是否为空
        if (StrUtil.isBlank(goodsEntity.getGoodsName())) {
            return fail(JsonResultCode.GOODS_NAME_IS_NULL);
        }

        //如果商品使用默认的规格形式，也需要根据默认形式设置一个GoodsSpecProducts参数
        if (goodsEntity.getGoodsSpecProducts() == null || goodsEntity.getGoodsSpecProducts().size() == 0) {
            return fail(JsonResultCode.GOODS_SPEC_ATTRIBUTE_SPEC_K_V_CONFLICT);
        }

        //判断商品主图是否为空
        if (StrUtil.isBlank(goodsEntity.getGoodsImg())) {
            return fail(JsonResultCode.MEDICAL_GOODS_MAIN_IMG_IS_NULL);
        }
        try {
            shop().medicalGoodsService.insert(shopId(), goodsEntity);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return fail(JsonResultCode.MEDICAL_GOODS_SKU_CONTENT_ILLEGAL, e.getMessage());
        }

        return success();
    }

    @PostMapping("/api/admin/medical/goods/batch")
    public JsonResult batchOperate(@RequestBody MedicalGoodsBatchOperateParam param) {
        shop().medicalGoodsService.batchOperate(param);
        return success();
    }

    /**
     * 药品修改
     * @param goodsEntity
     * @return
     */
    @PostMapping("/api/admin/medical/goods/update")
    public JsonResult update(@RequestBody GoodsEntity goodsEntity) {

        if (goodsEntity.getGoodsId() == null) {
            return fail(JsonResultCode.GOODS_ID_IS_NULL);
        }
        //判断商品名称是否为空
        if (StrUtil.isBlank(goodsEntity.getGoodsName())) {
            return fail(JsonResultCode.GOODS_NAME_IS_NULL);
        }
        //如果商品使用默认的规格形式，也需要根据默认形式设置一个GoodsSpecProducts参数
        if (goodsEntity.getGoodsSpecProducts() == null || goodsEntity.getGoodsSpecProducts().size() == 0) {
            return fail(JsonResultCode.GOODS_SPEC_ATTRIBUTE_SPEC_K_V_CONFLICT);
        }

        //判断商品主图是否为空
        if (StrUtil.isBlank(goodsEntity.getGoodsImg())) {
            return fail(JsonResultCode.MEDICAL_GOODS_MAIN_IMG_IS_NULL);
        }

        try {
            shop().medicalGoodsService.update(shopId(), goodsEntity);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return fail(JsonResultCode.MEDICAL_GOODS_SKU_CONTENT_ILLEGAL, e.getMessage());
        }

        return success();
    }

    /**
     * 药品删除
     * @param goodsId 药品id
     * @return
     */
    @GetMapping("/api/admin/medical/goods/delete/{goodsId}")
    public JsonResult delete(@PathVariable("goodsId") Integer goodsId) {
        if (goodsId == null) {
            return fail(JsonResultCode.GOODS_ID_IS_NULL);
        }

        shop().medicalGoodsService.deleteByGoodsId(goodsId);

        return success();
    }

    /**
     * 药品查询
     * @param goodsId 药品id
     * @return
     */
    @GetMapping("/api/admin/medical/goods/get/{goodsId}")
    public JsonResult getByGoodsId(@PathVariable("goodsId") Integer goodsId) {
        if (goodsId == null) {
            return fail(JsonResultCode.GOODS_ID_IS_NULL);
        }
        GoodsDetailVo goodsDetailVo = shop().medicalGoodsService.getGoodsDetailByGoodsId(goodsId);

        return success(goodsDetailVo);
    }

    /**
     * 药品分页查询
     * @param pageListParam 分页信息
     * @return
     */
    @PostMapping("/api/admin/medical/goods/page/list")
    public JsonResult getGoodsPageList(@RequestBody MedicalGoodsPageListParam pageListParam) {
        PageResult<GoodsPageListVo> goodsPageList = shop().medicalGoodsService.getGoodsPageList(pageListParam);

        return success(goodsPageList);
    }

    @PostMapping("/api/admin/medical/external/page/list")
    public JsonResult getExternalPageList(@RequestBody GoodsExternalPageParam param) {
        PageResult<GoodsExternalDo> externalPageList = shop().medicalGoodsService.getExternalPageList(param);
        return success(externalPageList);
    }

    @PostMapping("/api/admin/medical/external/save/matched/goods")
    public JsonResult saveMatchedGoodsList(@RequestBody List<ExternalMatchedGoodsParam> param) {
        boolean alreadyDisposed = shop().medicalGoodsService.isAlreadyDisposed(param);
        if (alreadyDisposed) {
            return fail(JsonResultCode.ALREADY_DISPOSED);
        }
        shop().medicalGoodsService.batchSaveMatchedGoodsList(param);
        return success();
    }

    @PostMapping("/api/admin/medical/external/fail/match/goods")
    public JsonResult failMatchGoods(@RequestBody FailMatchedParam param){
        shop().medicalGoodsService.failMatchGoods(param);
        return success();
    }

    @PostMapping("/api/admin/medical/goods/pull")
    public JsonResult fetchExternalMedicalInfo() {
        return shop().medicalGoodsService.fetchExternalMedicalInfo();
    }

    @PostMapping("/api/admin/medical/store/goods/pull")
    public JsonResult fetchExternalStoreMedicalInfo() {
        shop().medicalGoodsService.fetchExternalStoresGoodsInfo();
        return success();
    }

    @PostMapping("/api/admin/medical/up/store/goods")
    public JsonResult batchOnStoreGoods(){

        return success();
    }

    @PostMapping("/api/admin/medical/store/goods/pull2")
    public JsonResult fetchExternalStoreMedicalInfoForTest(@RequestBody MedicalGoodsExternalStoreRequestParam param) {
        return success(shop().medicalGoodsService.fetchExternalStoreTest(param));
    }

    @Autowired
    OrderStoreSyncService orderStoreSyncService;

    @PostMapping("/api/admin/medical/test1")
    public JsonResult fetchTest(@RequestBody OrderStorePosBo param) {
        boolean b = orderStoreSyncService.pushOrderInfoToStore(param);
        return success(b);
    }

    @PostMapping("/api/admin/medical/test2")
    public JsonResult fetchTest(@RequestBody StoreGoodsNumConfirmParam param) {
        StoreGoodsConfirmVo vo = orderStoreSyncService.syncGoodsInfosFromStore(param);
        return success(vo);
    }

    @PostMapping("/api/admin/medical/test3")
    public JsonResult fetchTest(@RequestBody OrderStockEnoughQueryParam param) {
        OrderStockEnoughQueryVo vo = orderStoreSyncService.getStockEnoughShopList(param);
        return success(vo);
    }

    @PostMapping("/api/admin/medical/test4")
    public JsonResult fetchTest(@RequestBody OrderStoreCancelParam param) {
       boolean b =  orderStoreSyncService.cancelOrder(param);
        return success(b);
    }
}
