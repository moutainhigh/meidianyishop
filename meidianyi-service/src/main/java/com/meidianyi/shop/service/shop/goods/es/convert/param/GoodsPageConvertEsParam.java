package com.meidianyi.shop.service.shop.goods.es.convert.param;

import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.es.*;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListParam;
import com.meidianyi.shop.service.shop.goods.es.convert.exception.ParamConvertException;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsSearchFieldsConstant;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsSearchSource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GoodsPageListParam to EsSearchParam
 *
 * @author 卢光耀
 * @date 2019/11/1 5:30 下午
 */
public class GoodsPageConvertEsParam implements EsParamConvertInterface {

    private GoodsPageListParam param;

    @Override
    public EsSearchParam convert(Object object, Integer shopId) {
        if (object instanceof GoodsPageListParam) {
            param = (GoodsPageListParam) object;
            return assemblyEsSearchParam(shopId);
        } else {
            throw new ParamConvertException("object is not instanceof GoodsPageListParam,can't convert");
        }
    }

    private EsSearchParam assemblyEsSearchParam(Integer shopId) {
        EsSearchParam searchParam = new EsSearchParam();
        List<FieldProperty> propertyList = new ArrayList<>();
        if (param.getIsFactQuery() && param.getFactNameList() != null) {
            searchParam.setFactList(
                param.getFactNameList().stream().map(this::getFactByName).collect(Collectors.toList())
            );
        }

        addProperties(shopId, propertyList);

        if (null != param.getPageRows()) {
            searchParam.setPageRows(param.getPageRows());
        }
        if (null != param.getCurrentPage()) {
            searchParam.setCurrentPage(param.getCurrentPage());
        }
        if (StringUtils.isNotBlank(param.getOrderDirection()) && StringUtils.isNotBlank(param.getOrderField())) {
            List<Sort> sorts = Lists.newArrayList();
            Sort sort = new Sort();
            if (GoodsPageListParam.GOODS_NUMBER.equals(param.getOrderField())) {
                sort.setSortName(EsSearchName.GOODS_NUMBER);
            } else if (GoodsPageListParam.GOODS_SALE_NUM.equals(param.getOrderField())) {
                sort.setSortName(EsSearchName.GOODS_SALE_NUM);
            } else if (GoodsPageListParam.SHOP_PRICE.equals(param.getOrderField())) {
                sort.setSortName(EsSearchName.SHOP_PRICE);
            }
            if (GoodsPageListParam.ASC.equals(param.getOrderDirection())) {
                sort.setSortOrder(SortOrder.ASC);
            } else {
                sort.setSortOrder(SortOrder.DESC);
            }
            sorts.add(sort);
            searchParam.setSorts(sorts);
        }
        if (!propertyList.isEmpty()) {
            searchParam.setSearchList(propertyList);
        }
        searchParam.setIncludes(EsSearchFieldsConstant.GOODS_SEARCH_STR);
        searchParam.setSearchSource(EsSearchSource.ADMIN);
        searchParam.setAnalyzerStatus(param.getOpenedAnalyzer());
        return searchParam;
    }

    /**
     * 属性填充
     *
     * @param shopId
     * @param propertyList
     */
    private void addProperties(Integer shopId, List<FieldProperty> propertyList) {
        if (!CollectionUtils.isEmpty(param.getGoodsIds())) {
            propertyList.add(new FieldProperty(EsSearchName.GOODS_ID, param.getGoodsIds()));
        }
        if (null != shopId) {
            propertyList.add(new FieldProperty(EsSearchName.SHOP_ID, shopId));
        }
        if (GoodsConstant.SALE_OUT.equals(param.getIsSaleOut())) {
            if (param.getSelectType().equals(GoodsPageListParam.GOODS_PRD_LIST)) {
                propertyList.add(new FieldProperty(EsSearchName.Prd.PRD_NUMBER, 0, Operator.EQ));
            } else {
                propertyList.add(new FieldProperty(EsSearchName.GOODS_NUMBER, 0, Operator.EQ));
            }

        } else if (GoodsConstant.NOT_SALE_OUT.equals(param.getIsSaleOut())) {
            propertyList.add(new FieldProperty(EsSearchName.GOODS_NUMBER, 0, Operator.GT));
        }
        if (StringUtils.isNotBlank(param.getGoodsName())) {
            propertyList.add(new FieldProperty(EsSearchName.KEY_WORDS, param.getGoodsName(), Operator.SIM));
        }
        if (null != param.getHighShopPrice()) {
            propertyList.add(new FieldProperty(EsSearchName.SHOP_PRICE, param.getHighShopPrice(), Operator.LTE));
        }
        if (null != param.getLowShopPrice()) {
            propertyList.add(new FieldProperty(EsSearchName.SHOP_PRICE, param.getLowShopPrice(), Operator.GTE));
        }
        if (null != param.getCatId()) {
            propertyList.add(new FieldProperty(EsSearchName.FULL_CAT_ID, param.getCatId()));
        }
        if (null != param.getSortId()) {
            propertyList.add(new FieldProperty(EsSearchName.FULL_SORT_ID, param.getSortId()));
        }
        if (null != param.getLabelId()) {
            propertyList.add(new FieldProperty(EsSearchName.GOODS_LABEL, param.getLabelId()));
        }
        if (null != param.getSaleTimeStart()) {
            propertyList.add(new FieldProperty(
                EsSearchName.SALE_TIME, DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL, param.getSaleTimeStart()), Operator.GTE));

        }
        if (null != param.getSaleTimeEnd()) {
            propertyList.add(new FieldProperty(EsSearchName.SALE_TIME, DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL, param.getSaleTimeEnd()), Operator.LTE));

        }
        if (null != param.getBrandId()) {
            propertyList.add(new FieldProperty(EsSearchName.BRAND_ID, param.getBrandId()));
        }
        if (null != param.getSource()) {
            propertyList.add(new FieldProperty(EsSearchName.SOURCE, param.getSource()));
        }
        if (null != param.getGoodsType()) {
            propertyList.add(new FieldProperty(EsSearchName.GOODS_TYPE, param.getGoodsType()));
        }

        if (null != param.getIsOnSale()) {
            propertyList.add(new FieldProperty(EsSearchName.IS_ON_SALE, param.getIsOnSale()));
        }
        if (CollectionUtils.isNotEmpty(param.getNotIncludeGoodsIds())) {
            propertyList.add(
                new FieldProperty(EsSearchName.GOODS_ID, param.getNotIncludeGoodsIds(), Operator.EQ, QueryType.MUST_NOT)
            );
        }
    }
}
