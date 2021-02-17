package com.meidianyi.shop.service.shop.goods.es.convert.param;

import com.google.common.collect.Lists;
import com.meidianyi.shop.service.pojo.shop.goods.es.*;
import com.meidianyi.shop.service.pojo.wxapp.goods.search.GoodsSearchMpParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.search.SortItemEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luguangyao
 */
public class GoodsSearchMpConverter implements EsParamConvertInterface  {
    private GoodsSearchMpParam param;


    @Override
    public EsSearchParam convert(Object object, Integer shopId) {
        if( object instanceof GoodsSearchMpParam ){
            param = (GoodsSearchMpParam)object;
            return assemblyEsSearchParam(param,shopId);
        }else{
            return null;
        }
    }

    private EsSearchParam assemblyEsSearchParam(GoodsSearchMpParam param,Integer shopId){
        List<FieldProperty> propertyList = new ArrayList<>();
        EsSearchParam searchParam = new EsSearchParam();
        searchParam.setQueryByPage(true);
        searchParam.setCurrentPage(param.getCurrentPage());
        searchParam.setPageRows(param.getPageRows());
        propertyList.add(new FieldProperty(EsSearchName.IS_ON_SALE,1));
        if (null != shopId) {
            propertyList.add(new FieldProperty(EsSearchName.SHOP_ID, shopId));
        }
        //关键字查询支持：
        //1.商品名称（模糊查询）
        //2.商品品牌（精确查询）
        if (StringUtils.isNotBlank(param.getKeyWords())) {
            propertyList.add(new FieldProperty(EsSearchName.KEY_WORDS, param.getKeyWords()));
//            propertyList.add(new FieldProperty(EsSearchName.BRAND_NAME,param.getKeyWords()));
        }
        /* 是否展示售罄商品 */
        if (param.getSoldOutGoodsShow()) {
            propertyList.add(new FieldProperty(EsSearchName.GOODS_NUMBER, 0, Operator.GTE));
        } else {
            propertyList.add(new FieldProperty(EsSearchName.GOODS_NUMBER, 0, Operator.GT));
        }
        if (null != param.getMinPrice()) {
            propertyList.add(new FieldProperty(EsSearchName.SHOW_PRICE, param.getMinPrice(), Operator.GTE));
        }
        if (null != param.getMaxPrice()) {
            propertyList.add(new FieldProperty(EsSearchName.SHOW_PRICE, param.getMaxPrice(), Operator.LTE));
        }
        if (!CollectionUtils.isEmpty(param.getSortIds())) {
            propertyList.add(new FieldProperty(EsSearchName.FULL_SORT_ID, param.getSortIds()));
        }
        if (!CollectionUtils.isEmpty(param.getBrandIds())) {
            propertyList.add(new FieldProperty(EsSearchName.BRAND_ID,param.getBrandIds()));
        }
        if( !CollectionUtils.isEmpty(param.getActivityTypes()) ){
            propertyList.add(new FieldProperty(EsSearchName.GOODS_TYPE,param.getActivityTypes()));
        }
        if( !CollectionUtils.isEmpty( param.getGoodsIds()) ){
            propertyList.add(new FieldProperty(EsSearchName.GOODS_ID,param.getGoodsIds()));
        }
        if( !propertyList.isEmpty() ){
            searchParam.setSearchList(propertyList);
        }
        List<Sort> sorts = Lists.newArrayList();
        boolean useUserSort = param.getSortItem() != null
            && param.getSortItem() != SortItemEnum.NULL
            && param.getSortDirection() != null ;
        if( useUserSort ){
            sorts.add(getSort(param.getSortItem(),param.getSortDirection()));
        }
        if( !useUserSort && param.getShopSortItem() != null && param.getShopSortDirection() != null){
            sorts.add(getSort(param.getShopSortItem(),param.getShopSortDirection()));
        }
        if( !CollectionUtils.isEmpty(sorts) ){
            searchParam.setSorts(sorts);
        }
        return searchParam;
    }


}
