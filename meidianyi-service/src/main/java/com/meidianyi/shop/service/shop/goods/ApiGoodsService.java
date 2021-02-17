package com.meidianyi.shop.service.shop.goods;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.api.ApiBasePageParam;
import com.meidianyi.shop.common.foundation.util.api.ApiPageResult;
import com.meidianyi.shop.common.pojo.saas.api.ApiJsonResult;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalGateConstant;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductBakRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductRecord;
import com.meidianyi.shop.db.shop.tables.records.StoreRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.api.*;
import com.meidianyi.shop.service.pojo.shop.goods.pos.PosSyncGoodsPrdParam;
import com.meidianyi.shop.service.pojo.shop.goods.pos.PosSyncProductMqParam;
import com.meidianyi.shop.service.pojo.shop.goods.pos.PosSyncProductParam;
import com.meidianyi.shop.service.pojo.shop.goods.pos.PosSyncStockParam;
import com.meidianyi.shop.service.shop.store.store.StoreGoodsService;
import com.meidianyi.shop.service.shop.store.store.StoreService;
import org.jooq.Condition;
import org.jooq.SelectConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.GOODS_IMG;
import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;

/**
 * api 外部对接使用
 * @author 李晓冰
 * @date 2020年05月29日
 */
@Service
public class ApiGoodsService extends ShopBaseService {

    @Autowired
    public GoodsService goodsService;
    @Autowired
    public GoodsSpecProductService goodsSpecProductService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreGoodsService storeGoodsService;
    @Autowired
    public GoodsSortService goodsSort;

    /**
     * pos同步商品信息 上下架和价格
     * @param posSyncProductParam
     * @return
     */
    public ApiJsonResult posSyncProductMq(PosSyncProductParam posSyncProductParam){
        ApiJsonResult apiJsonResult = new ApiJsonResult();
        Integer posShopId = posSyncProductParam.getShopId();
        if (posShopId == null) {
            apiJsonResult.setCode(ApiExternalGateConstant.ERROR_CODE_SYNC_FAIL);
            apiJsonResult.setMsg("缺少必传参数shop_id");
            return  apiJsonResult;
        }
        StoreRecord storeRecord = storeService.getStoreByPosShopId(posShopId);
        if (storeRecord == null) {
            apiJsonResult.setCode(ApiExternalGateConstant.ERROR_CODE_SYNC_FAIL);
            apiJsonResult.setMsg("该店铺没有对应的门店");
            return  apiJsonResult;
        }

        if (posSyncProductParam.getGoodsList() == null || posSyncProductParam.getGoodsList().size() == 0) {
            apiJsonResult.setCode(ApiExternalGateConstant.ERROR_CODE_SYNC_FAIL);
            apiJsonResult.setMsg("缺少商品");
            return  apiJsonResult;
        }
        PosSyncProductMqParam mqParam = new PosSyncProductMqParam();
        mqParam.setShopId(getShopId());
        mqParam.setStoreId(storeRecord.getStoreId());
        mqParam.setGoodsPrdList(posSyncProductParam.getGoodsList());
        // 调用消息队列
        saas.taskJobMainService.dispatchImmediately(mqParam, PosSyncProductMqParam.class.getName(), getShopId(),
            TaskJobsConstant.TaskJobEnum.POS_SYNC_PRODUCT.getExecutionType());
//        posSyncProductMqCallback(storeRecord.getStoreId(),posSyncProductParam.getGoodsList());
        return apiJsonResult;
    }

    /**
     * pos 同步商品规格信息
     * @param storeId 门店id
     * @param goodsPrdList 规格同步信息
     */
    public void posSyncProductMqCallback(Integer storeId,List<PosSyncGoodsPrdParam> goodsPrdList){
        Map<String, PosSyncGoodsPrdParam> posPrdMap = goodsPrdList.stream().collect(Collectors.toMap(PosSyncGoodsPrdParam::getPrdSn, Function.identity()));
        // 过滤掉prdSn无法匹配上的
        List<GoodsSpecProductRecord> goodsSpecPrdBySns = goodsSpecProductService.getGoodsSpecPrdBySn(posPrdMap.keySet());

        // 待更新规格条码字段的规格集合
        List<GoodsSpecProductRecord> goodsSpecPrdReadyToUpdate = new ArrayList<>(goodsSpecPrdBySns.size()/2);
        List<PosSyncGoodsPrdParam> storePrdReadyToUpdate = new ArrayList<>(posPrdMap.size());
        goodsSpecPrdBySns.forEach(prdRecord->{
            PosSyncGoodsPrdParam posSyncGoodsPrdParam = posPrdMap.get(prdRecord.getPrdSn());
            if (!Objects.equals(prdRecord.getPrdCodes(), posSyncGoodsPrdParam.getPrdCodes())) {
                prdRecord.setPrdCodes(posSyncGoodsPrdParam.getPrdCodes());
                goodsSpecPrdReadyToUpdate.add(prdRecord);
            }

            posSyncGoodsPrdParam.setPrdId(prdRecord.getPrdId());
            storePrdReadyToUpdate.add(posSyncGoodsPrdParam);
        });
        db().batchUpdate(goodsSpecPrdReadyToUpdate).execute();
        storeGoodsService.batchUpdateForSyncPosProduct(storeId,storePrdReadyToUpdate);
    }

    /**
     * pos 对接 同步规格数量
     * @param param
     * @return
     */
    public ApiJsonResult posSyncStock(PosSyncStockParam param){
        ApiJsonResult apiJsonResult = new ApiJsonResult();
        Integer posShopId = param.getPosShopId();
        if (posShopId == null) {
            apiJsonResult.setCode(ApiExternalGateConstant.ERROR_CODE_SYNC_FAIL);
            apiJsonResult.setMsg("缺少必传参数shop_id");
            return  apiJsonResult;
        }

        StoreRecord storeRecord = storeService.getStoreByPosShopId(posShopId);
        if (storeRecord == null) {
            apiJsonResult.setCode(ApiExternalGateConstant.ERROR_CODE_SYNC_FAIL);
            apiJsonResult.setMsg("该店铺没有对应的门店");
            return  apiJsonResult;
        }

        List<GoodsSpecProductRecord> goodsSpecPrdBySns = goodsSpecProductService.getGoodsSpecPrdBySn(Collections.singleton(param.getPrdSn()));
        if (goodsSpecPrdBySns == null || goodsSpecPrdBySns.size() == 0) {
            apiJsonResult.setCode(ApiExternalGateConstant.ERROR_CODE_SYNC_FAIL);
            apiJsonResult.setMsg("该prd_sn没有对应的商品");
            return  apiJsonResult;
        }
        Integer prdId = goodsSpecPrdBySns.get(0).getPrdId();
        Integer prdNum = (int) (Math.floor(param.getNumber()));
        storeGoodsService.updatePrdNumForPosSyncStock(storeRecord.getStoreId(),prdId,prdNum);
        return apiJsonResult;
    }

    /**
     * erp-ekb 获取指定时间范围内未删除在售的商品
     * @param pageParam 分页参数
     * @return 商品列表信息
     */
    public ApiGoodsPageResult apiGetGoodsList(ApiBasePageParam pageParam) {
        Condition condition = GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GOODS.IS_ON_SALE.eq(GoodsConstant.ON_SALE));

        if (pageParam.getStartTime() != null) {
            condition = condition.and(GOODS.UPDATE_TIME.gt(pageParam.getStartTime()));
        }
        if (pageParam.getEndTime() != null) {
            condition = condition.and(GOODS.UPDATE_TIME.lt(pageParam.getEndTime()));
        }

        SelectConditionStep<?> selectConditionStep = db().select(GOODS.GOODS_ID, GOODS.GOODS_SN, GOODS.GOODS_NAME,GOODS.GOODS_IMG, GOODS.CREATE_TIME, GOODS.UPDATE_TIME,GOODS.SORT_ID)
            .from(GOODS).where(condition);

        // 查询分页结果，并将统一的ApiPageResult转换为商品特定的分页结果对象
        ApiPageResult<GoodsRecord> apiPageResult = this.getApiPageResult(selectConditionStep, pageParam.getPage(), pageParam.getPageSize(), GoodsRecord.class);

        List<ApiGoodsListVo> apiGoodsListVos = new ArrayList<>(apiPageResult.getDataList().size());

        List<Integer> goodsIds = new ArrayList<>(apiPageResult.getDataList().size());
        List<Integer> sortIds = new ArrayList<>(apiPageResult.getDataList().size());
        for (GoodsRecord goodsRecord : apiPageResult.getDataList()) {
            apiGoodsListVos.add(new ApiGoodsListVo(goodsRecord));
            goodsIds.add(goodsRecord.getGoodsId());
            sortIds.add(goodsRecord.getSortId());
        }
        Map<Integer, String> sortNameMap = goodsSort.apiGetSortNameMap(sortIds);
        Map<Integer, List<GoodsSpecProductRecord>> specPrdMap = goodsSpecProductService.apiGetGoodsSpecPrdMapByGoodsIds(goodsIds);

        apiGoodsListVos.removeIf(apiGoodsListVo -> {
            List<GoodsSpecProductRecord> specPrdList = specPrdMap.get(apiGoodsListVo.getGoodsId());
            if (specPrdList == null || specPrdList.size() == 0) {
                return true;
            }

            List<ApiGoodsSkuVo> apiGoodsSkuVos = new ArrayList<>();
            for (GoodsSpecProductRecord specProductRecord : specPrdList) {
                ApiGoodsSkuVo apiGoodsSkuVo = new ApiGoodsSkuVo(specProductRecord);
                apiGoodsSkuVo.setPrdImg(goodsService.getImgFullUrlUtil(apiGoodsSkuVo.getPrdImg()));
                apiGoodsSkuVos.add(apiGoodsSkuVo);
            }

            apiGoodsListVo.setGoodsImg(goodsService.getImgFullUrlUtil(apiGoodsListVo.getGoodsImg()));
            apiGoodsListVo.setCatName(sortNameMap.get(apiGoodsListVo.getSortId()));
            apiGoodsListVo.setSkuCount(apiGoodsSkuVos.size());
            apiGoodsListVo.setSkuList(apiGoodsSkuVos);

            return false;
        });

        ApiGoodsPageResult goodsPageResult = new ApiGoodsPageResult();
        goodsPageResult.setCurPageNo(apiPageResult.getCurPageNo());
        goodsPageResult.setPageSize(apiPageResult.getPageSize());
        goodsPageResult.setTotalGoodsCount(apiPageResult.getTotalCount());
        goodsPageResult.setGoodsList(apiGoodsListVos);
        return goodsPageResult;
    }

    /**
     * erp-ekb 获取指定商品信息(包含已删除商品)
     * @param param
     * @return
     */
    public ApiGoodsDetailVo apiGetSingleGoods(ApiGoodsDetailParam param){
        GoodsRecord goodsRecord = db().select(GOODS.GOODS_ID, GOODS.GOODS_SN, GOODS.GOODS_NAME, GOODS.GOODS_IMG, GOODS.CREATE_TIME, GOODS.UPDATE_TIME, GOODS.SORT_ID, GOODS.DEL_FLAG)
            .from(GOODS).where(GOODS.GOODS_ID.eq(param.getGoodsId())).fetchAnyInto(GoodsRecord.class);
        if (goodsRecord == null) {
            return null;
        }
        ApiGoodsDetailVo goodsDetailVo = new ApiGoodsDetailVo(goodsRecord);
        // 处理商品图片
        List<String> imgList = db().select(GOODS_IMG.IMG_URL).from(GOODS_IMG).where(GOODS_IMG.GOODS_ID.eq(goodsDetailVo.getGoodsId())).fetch(GOODS_IMG.IMG_URL);
        List<String> imgFullList =  new ArrayList<>();
        imgFullList.add(goodsService.getImgFullUrlUtil(goodsDetailVo.getGoodsImg()));
        for (String s : imgList) {
            imgFullList.add(goodsService.getImgFullUrlUtil(s));
        }
        goodsDetailVo.setGoodsImgs(imgFullList);

        // 设置分类
        Map<Integer, String> sortNameMap = goodsSort.apiGetSortNameMap(Collections.singletonList(goodsDetailVo.getSortId()));
        goodsDetailVo.setCatName(sortNameMap.get(goodsDetailVo.getSortId()));

        // 处理规格信息
        // 需要处理商品已经被删除情况
        Map<Integer, List<GoodsSpecProductRecord>> specPrdMap = goodsSpecProductService.apiGetGoodsSpecPrdMapByGoodsIds(Collections.singletonList(goodsDetailVo.getGoodsId()));
        List<GoodsSpecProductBakRecord> goodsSpecProductBakRecords = goodsSpecProductService.apiGetGoodsSpecPrdDeletedMbyGoodsId(goodsDetailVo.getGoodsId());
        List<GoodsSpecProductRecord> specPrdList = specPrdMap.get(goodsDetailVo.getGoodsId());

        List<ApiGoodsSkuVo> apiGoodsSkuVos = new ArrayList<>(2);
        if (specPrdList != null) {
            for (GoodsSpecProductRecord specProductRecord : specPrdList) {
                ApiGoodsSkuVo apiGoodsSkuVo = new ApiGoodsSkuVo(specProductRecord);
                apiGoodsSkuVo.setPrdImg(goodsService.getImgFullUrlUtil(apiGoodsSkuVo.getPrdImg()));
                apiGoodsSkuVos.add(apiGoodsSkuVo);
            }
        }

        for (GoodsSpecProductBakRecord goodsSpecProductBakRecord : goodsSpecProductBakRecords) {
            ApiGoodsSkuVo apiGoodsSkuVo = new ApiGoodsSkuVo(goodsSpecProductBakRecord);
            apiGoodsSkuVo.setPrdImg(goodsService.getImgFullUrlUtil(apiGoodsSkuVo.getPrdImg()));
            apiGoodsSkuVos.add(apiGoodsSkuVo);
        }

        goodsDetailVo.setSkuList(apiGoodsSkuVos);
        goodsDetailVo.setSkuCount(apiGoodsSkuVos.size());
        return goodsDetailVo;
    }

    /**
     * erp-ekb 商品库存同步
     * @param param 库存参数
     * @return result
     */
    public ApiJsonResult syncStock(ApiSyncStockParam param){
        ApiJsonResult result = new ApiJsonResult();

        Map<Integer, GoodsSpecProductRecord> skuIdMap = goodsSpecProductService.apiGetGoodsSpecPrdByPrdId(param.getSkuId());
        if (skuIdMap == null) {
            result.setCode(ApiExternalGateConstant.ERROR_CODE_SYNC_FAIL);
            result.setMsg("同步数据的规格信息不存在");
            return result;
        }

        GoodsSpecProductRecord specProductRecord = skuIdMap.get(param.getSkuId());
        specProductRecord.setPrdNumber(param.getGoodsNum());

        Integer goodsNum = 0;
        for (Map.Entry<Integer, GoodsSpecProductRecord> entry : skuIdMap.entrySet()) {
            goodsNum += entry.getValue().getPrdNumber();
        }
        GoodsRecord goodsRecord = db().select(GOODS.GOODS_ID,GOODS.GOODS_NUMBER).from(GOODS).where(GOODS.GOODS_ID.eq(specProductRecord.getGoodsId())).fetchAnyInto(GoodsRecord.class);
        goodsRecord.setGoodsNumber(goodsNum);
        try {
            transaction(()->{
                db().executeUpdate(goodsRecord);
                db().executeUpdate(specProductRecord);
            });
            goodsService.updateEs(Collections.singletonList(goodsRecord.getGoodsId()));
        } catch (Exception exception) {
            result.setCode(ApiExternalGateConstant.ERROR_CODE_SYNC_FAIL);
            result.setMsg("数据同步错误");
            return result;
        }
        return result;
    }
}
