package com.meidianyi.shop.service.shop.goods.es.convert.param;

import com.google.common.collect.Lists;
import com.meidianyi.shop.service.pojo.shop.goods.es.*;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpParam;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsSearchSource;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpParam.*;

/**
 * GoodsListMpConverter
 *
 * @author 卢光耀
 * @date 2019/11/14 5:41 下午
 */
public class GoodsListMpConverter implements EsParamConvertInterface {

    private GoodsListMpParam param;


    @Override
    public EsSearchParam convert(Object object, Integer shopId) {
        if (object instanceof GoodsListMpParam) {
            param = (GoodsListMpParam) object;
            return assemblyEsSearchParam(param, shopId);
        } else {
            return null;
        }
    }

    private EsSearchParam assemblyEsSearchParam(GoodsListMpParam param, @NotNull Integer shopId) {
        List<FieldProperty> propertyList = new ArrayList<>();
        List<Sort> sorts = Lists.newArrayList();
        EsSearchParam searchParam = new EsSearchParam();
        if (null != param.getPageRows()) {
            searchParam.setPageRows(param.getPageRows());
        }
        if (null != param.getCurrentPage()) {
            searchParam.setCurrentPage(param.getCurrentPage());
        }
        if (null != shopId) {
            propertyList.add(new FieldProperty(EsSearchName.SHOP_ID, shopId));
        }

        if (param.getRecommendType().equals((byte) 1)) {
            //手动推荐
            searchParam.setQueryByPage(false);
            propertyList.add(new FieldProperty(EsSearchName.GOODS_ID, param.getGoodsItems()));
        } else if (param.getRecommendType().equals((byte) 0)) {
            autoRecommendCondition(param, propertyList, sorts, searchParam);
        }
        if (sorts.size() == 0 && param.getShopSortItem() != null && param.getShopSortDirection() != null) {
            sorts.add(getSort(param.getShopSortItem(), param.getShopSortDirection()));
        }
        if (!CollectionUtils.isEmpty(sorts)) {
            searchParam.setSorts(sorts);
        }

        if (!propertyList.isEmpty()) {
            searchParam.setSearchList(propertyList);
        }
        searchParam.setSearchSource(EsSearchSource.WX_APP);
        return searchParam;
    }

    /**
     * 自动推荐条件填充
     *
     * @param param
     * @param propertyList
     * @param sorts
     * @param searchParam
     */
    private void autoRecommendCondition(GoodsListMpParam param, List<FieldProperty> propertyList, List<Sort> sorts, EsSearchParam searchParam) {
        //自动推荐
        searchParam.setPageRows(param.getGoodsNum());
        searchParam.setQueryByPage(true);
        /* 是否已售罄*/
        if (!param.getSoldOutGoodsShow()) {
            propertyList.add(new FieldProperty(EsSearchName.IS_ON_SALE, 1));
            propertyList.add(new FieldProperty(EsSearchName.GOODS_NUMBER, 0, Operator.GT));
        }
        /* 活动商品*/
        if (param.getGoodsType() != null && param.getGoodsType() != 0) {
            propertyList.add(new FieldProperty(EsSearchName.GOODS_TYPE, param.getGoodsType()));
        }
        /* 关键词*/
        if (StringUtils.isNotBlank(param.getKeywords())) {
            propertyList.add(new FieldProperty(EsSearchName.KEY_WORDS, param.getKeywords()));
        }
        /* 商品最高价 */
        if (param.getMaxPrice() != null) {
            propertyList.add(new FieldProperty(EsSearchName.SHOW_PRICE, param.getMaxPrice(), Operator.LTE));
        }
        /* 商品最低价 */
        if (param.getMinPrice() != null) {
            propertyList.add(new FieldProperty(EsSearchName.SHOW_PRICE, param.getMinPrice(), Operator.GTE));
        }
        /* 是否展示售罄商品 */
        if (param.getSoldOutGoodsShow()) {
            propertyList.add(new FieldProperty(EsSearchName.GOODS_NUMBER, 0, Operator.GTE));
        } else {
            propertyList.add(new FieldProperty(EsSearchName.GOODS_NUMBER, 0, Operator.GT));
        }
        /* 商品范围*/
        if (StringUtils.isNotBlank(param.getGoodsArea())) {
            String goodsArea = param.getGoodsArea();
            if (!CollectionUtils.isEmpty(param.getGoodsAreaData())) {
                if ((BRAND_AREA).equals(goodsArea)) {
                    propertyList.add(new FieldProperty(EsSearchName.BRAND_ID, param.getGoodsAreaData()));
                } else if ((CAT_AREA).equals(goodsArea)) {
                    propertyList.add(new FieldProperty(EsSearchName.FULL_CAT_ID, param.getGoodsAreaData()));
                } else if ((LABEL_AREA).equals(goodsArea)) {
                    /*商品标签查goodsId*/
                    propertyList.add(new FieldProperty(EsSearchName.GOODS_ID, param.getGoodsItems()));
                } else if ((SORT_AREA).equals(goodsArea)) {
                    propertyList.add(new FieldProperty(EsSearchName.FULL_SORT_ID, param.getGoodsAreaData()));
                }
            }
        }


        if (param.getSortType() != null) {
            switch (param.getSortType()) {
                case 1:
                    sorts.add(getSort(EsSearchName.SALE_TIME, SortOrder.DESC));
                    break;
                case 2:
                    sorts.add(getSort(EsSearchName.TOTAL_SALE_NUMBER, SortOrder.DESC));
                    break;
                case 3:
                    sorts.add(getSort(EsSearchName.SHOW_PRICE, SortOrder.ASC));
                default:
                    break;
            }
        }
    }
}
