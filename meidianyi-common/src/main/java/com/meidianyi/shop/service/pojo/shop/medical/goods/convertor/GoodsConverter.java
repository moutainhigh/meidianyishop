package com.meidianyi.shop.service.pojo.shop.medical.goods.convertor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.pojo.shop.table.GoodsMedicalInfoDo;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsDo;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsExternalDo;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsPageListCondition;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsSortItem;
import com.meidianyi.shop.service.pojo.shop.medical.goods.MedicalGoodsConstant;
import com.meidianyi.shop.service.pojo.shop.medical.goods.bo.GoodsMedicalExternalRequestItemBo;
import com.meidianyi.shop.service.pojo.shop.medical.goods.entity.GoodsEntity;
import com.meidianyi.shop.service.pojo.shop.medical.goods.entity.GoodsMedicalInfoEntity;
import com.meidianyi.shop.service.pojo.shop.medical.goods.param.ExternalMatchedGoodsParam;
import com.meidianyi.shop.service.pojo.shop.medical.goods.param.MedicalGoodsPageListParam;
import com.meidianyi.shop.service.pojo.shop.medical.sku.entity.GoodsSpecProductEntity;
import com.meidianyi.shop.service.pojo.shop.store.goods.StoreGoods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 李晓冰
 * @date 2020年07月08日
 */
public class GoodsConverter {
    public static final String ASC = "asc";

    public static GoodsPageListCondition convertPageListConditionFromPageListParam(MedicalGoodsPageListParam pageListParam) {
        GoodsPageListCondition goodsPageListCondition = new GoodsPageListCondition();
        FieldsUtil.assign(pageListParam, goodsPageListCondition);

        if (pageListParam.getOrderField() != null) {
            GoodsSortItem goodsSortItem = new GoodsSortItem();
            goodsSortItem.setColumnName(pageListParam.getOrderField());
            if (ASC.equals(pageListParam.getOrderDirection())) {
                goodsSortItem.setAsc(true);
            } else {
                goodsSortItem.setAsc(false);
            }
            goodsPageListCondition.setPageSortItems(Collections.singletonList(goodsSortItem));
        }
        return goodsPageListCondition;
    }

    /**
     * 外部药品信息类转换为GoodsDo类
     * @param bo
     * @return
     */
    public static GoodsDo convertGoodsMedicalExternalRequestItemBoToGoodsDo(GoodsMedicalExternalRequestItemBo bo) {
        GoodsDo goodsDo = new GoodsDo();
        goodsDo.setGoodsId(bo.getGoodsId());
        goodsDo.setGoodsSn(bo.getGoodsCode());
        goodsDo.setGoodsBarCode(bo.getGoodsBarCode());
        goodsDo.setGoodsName(bo.getGoodsCommonName());
        goodsDo.setShopPrice(bo.getGoodsPrice());
        goodsDo.setCostPrice(bo.getGoodsPrice());
        if (bo.getStorePrice() != null) {
            goodsDo.setMarketPrice(bo.getStorePrice());
        } else {
            goodsDo.setMarketPrice(bo.getGoodsPrice());
        }
        goodsDo.setGoodsNumber(bo.getGoodsNumber());
        goodsDo.setIsMedical(bo.getIsMedical());
        goodsDo.setUnit(bo.getGoodsBasicUnit());
        goodsDo.setStoreCode(bo.getStoreCode());
        goodsDo.setHisStatus(bo.getHisStatus());
        goodsDo.setStoreStatus(bo.getStoreStatus());
        goodsDo.setLastUpdateTime(bo.getLastUpdateTime());
        if (BaseConstant.EXTERNAL_ITEM_STATE_DELETE.equals(bo.getState())) {
            goodsDo.setDelFlag(DelFlag.DISABLE_VALUE);
        }
        return goodsDo;
    }

    public static GoodsExternalDo convertGoodsMedicalExternalRequestItemBoToExternalDo(GoodsMedicalExternalRequestItemBo bo){
        GoodsExternalDo goodsExternalDo =new GoodsExternalDo();
        goodsExternalDo.setGoodsPrice(bo.getGoodsPrice());
        goodsExternalDo.setGoodsNumber(bo.getGoodsNumber());
        goodsExternalDo.setGoodsCode(bo.getGoodsCode());
        goodsExternalDo.setGoodsBarCode(bo.getGoodsBarCode());
        goodsExternalDo.setGoodsCommonName(bo.getGoodsCommonName());
        goodsExternalDo.setIsMedical(bo.getIsMedical());
        goodsExternalDo.setGoodsAliasName(bo.getGoodsAliasName());
        goodsExternalDo.setGoodsQualityRatio(bo.getGoodsQualityRatio());
        goodsExternalDo.setIsRx(bo.getIsRx());
        goodsExternalDo.setInsuranceCode(bo.getInsuranceCode());
        goodsExternalDo.setInsuranceDatabaseName(bo.getInsuranceDatabaseName());
        goodsExternalDo.setGoodsBasicUnit(bo.getGoodsBasicUnit());
        goodsExternalDo.setGoodsPackageUnit(bo.getGoodsPackageUnit());
        goodsExternalDo.setGoodsUnitConvertFactor(bo.getGoodsUnitConvertFactor());
        goodsExternalDo.setGoodsEquivalentQuantity(bo.getGoodsEquivalentQuantity());
        goodsExternalDo.setGoodsEquivalentUnit(bo.getGoodsEquivalentUnit());
        goodsExternalDo.setGoodsApprovalNumber(bo.getGoodsApprovalNumber());
        goodsExternalDo.setGoodsProductionEnterprise(bo.getGoodsProductionEnterprise());
        goodsExternalDo.setState(bo.getState());
        if (BaseConstant.EXTERNAL_ITEM_STATE_DELETE.equals(bo.getState())) {
            goodsExternalDo.setIsDelete(DelFlag.DISABLE_VALUE);
        }
        return goodsExternalDo;
    }

    /**
     * 药店更新商品转换GoodsDo类
     * @param bo
     * @return
     */
    public static GoodsDo convertGoodsMedicalExternalRequestItemBoToGoodsDoForStore(GoodsMedicalExternalRequestItemBo bo) {
        GoodsDo goodsDo = new GoodsDo();
        goodsDo.setGoodsId(bo.getGoodsId());
        goodsDo.setStoreStatus(bo.getStoreStatus());
        goodsDo.setMarketPrice(bo.getStorePrice());
        if (BaseConstant.EXTERNAL_ITEM_STATE_DELETE.equals(bo.getState())) {
            goodsDo.setDelFlag(DelFlag.DISABLE_VALUE);
        }
        return goodsDo;
    }

    public static StoreGoods convertBoToStoreGoods(GoodsMedicalExternalRequestItemBo bo, Integer storeId) {
        StoreGoods storeGoods = new StoreGoods();
        storeGoods.setStoreId(storeId);
        storeGoods.setGoodsCommonName(bo.getGoodsCommonName());
        storeGoods.setGoodsQualityRatio(bo.getGoodsQualityRatio());
        storeGoods.setGoodsApprovalNumber(bo.getGoodsApprovalNumber());
        storeGoods.setGoodsProductionEnterprise(bo.getGoodsProductionEnterprise());
        storeGoods.setGoodsStoreSn(bo.getGoodsCode());
        storeGoods.setProductNumber(bo.getGoodsNumber());
        storeGoods.setProductPrice(bo.getGoodsPrice());
        storeGoods.setMedicalKey(bo.getGoodsKeyComposedByNameQualityEnterprise());
        return storeGoods;
    }

    public static GoodsMedicalInfoDo convertGoodsMedicalExternalRequestItemBoToGoodsMedicalInfoDo(GoodsMedicalExternalRequestItemBo bo) {
        GoodsMedicalInfoDo goodsMedicalInfoDo = new GoodsMedicalInfoDo();
        FieldsUtil.assign(bo, goodsMedicalInfoDo);
        goodsMedicalInfoDo.setId(bo.getMedicalId());
        if (BaseConstant.EXTERNAL_ITEM_STATE_DELETE.equals(bo.getState())) {
            goodsMedicalInfoDo.setIsDelete(DelFlag.DISABLE_VALUE);
        }

        return goodsMedicalInfoDo;
    }

    public static GoodsEntity convertExternalMatchedGoodsParamToGoodsEntity(ExternalMatchedGoodsParam param){
        GoodsEntity goodsEntity =new GoodsEntity();
        FieldsUtil.assign(param,goodsEntity);
        goodsEntity.setGoodsName(param.getGoodsCommonName());
        if (MedicalGoodsConstant.SOURCE_FROM_HIS.equals(param.getSource())) {
            goodsEntity.setShopPrice(param.getHisPrice());
            goodsEntity.setCostPrice(param.getHisPrice());
            goodsEntity.setMarketPrice(param.getStorePrice());
        } else {
            goodsEntity.setShopPrice(param.getStorePrice());
            goodsEntity.setCostPrice(param.getStorePrice());
            goodsEntity.setMarketPrice(param.getStorePrice());
        }
        goodsEntity.setIsOnSale(MedicalGoodsConstant.ON_SALE);
        goodsEntity.setGoodsNumber(MedicalGoodsConstant.MEDICAL_GOODS_DEFAULT_NUM);
        goodsEntity.setIsDefaultProduct(MedicalGoodsConstant.DEFAULT_SKU);
        goodsEntity.setStoreCode(param.getStoreGoodsCode());

        if (MedicalGoodsConstant.GOODS_IS_MEDICAL.equals(param.getIsMedical())) {
            GoodsMedicalInfoEntity medicalInfoEntity =new GoodsMedicalInfoEntity();
            FieldsUtil.assign(param,medicalInfoEntity);
            goodsEntity.setGoodsMedicalInfo(medicalInfoEntity);
        }
        List<GoodsSpecProductEntity> skus =new ArrayList<>();
        GoodsSpecProductEntity specProductEntity = new GoodsSpecProductEntity();
        specProductEntity.setPrdNumber(MedicalGoodsConstant.MEDICAL_GOODS_DEFAULT_NUM);
        specProductEntity.setPrdPrice(goodsEntity.getShopPrice());
        specProductEntity.setPrdCostPrice(goodsEntity.getCostPrice());
        specProductEntity.setPrdMarketPrice(goodsEntity.getMarketPrice());
        skus.add(specProductEntity);
        goodsEntity.setGoodsSpecProducts(skus);

        return goodsEntity;
    }

    public ExternalMatchedGoodsParam convertGoodsExternalDoToMatchedParam(GoodsExternalDo goodsExternalDo){
        ExternalMatchedGoodsParam param = new ExternalMatchedGoodsParam();
        param.setStorePrice(goodsExternalDo.getGoodsPrice());
        param.setFromStoreId(goodsExternalDo.getId());
        param.setStoreGoodsCode(goodsExternalDo.getGoodsCode());
        return  null;
    }
}
