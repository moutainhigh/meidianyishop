package com.meidianyi.shop.service.shop.goods.es.convert.param;

import com.meidianyi.shop.service.pojo.shop.goods.es.EsSearchParam;
import com.meidianyi.shop.service.pojo.shop.goods.es.Fact;
import com.meidianyi.shop.service.pojo.shop.goods.es.Sort;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.search.SortDirectionEnum;
import com.meidianyi.shop.service.pojo.wxapp.goods.search.SortItemEnum;
import org.elasticsearch.search.sort.SortOrder;

/**
 * param转换接口
 *
 * @author 卢光耀
 * @date 2019/11/1 5:23 下午
 */
public interface EsParamConvertInterface {

    /**
     * GoodsPageListParam实现
     *
     * @param shopId 门店id
     * @param param  {@link GoodsPageListParam}
     * @return {@link EsSearchParam}
     */
    EsSearchParam convert(Object param, Integer shopId);


    /**
     * fact转化
     *
     * @param name {@link com.meidianyi.shop.service.pojo.shop.goods.es.Fact}
     * @return {@link Fact}
     */
    default Fact getFactByName(String name) {
        return Fact.builder().name(name)
            .fieldName(name)
            .build();
    }

    /**
     * 得到排序方式
     *
     * @param sortItemEnum
     * @param sortDirectionEnum
     * @return
     */
    default Sort getSort(SortItemEnum sortItemEnum, SortDirectionEnum sortDirectionEnum) {
        Sort sort = new Sort();
        if (GoodsPageListParam.ASC.equals(sortDirectionEnum.toString())) {
            sort.setSortOrder(SortOrder.ASC);
        } else {
            sort.setSortOrder(SortOrder.DESC);
        }
        sort.setSortName(sortItemEnum.getEsName());
        return sort;
    }

    /**
     * 得到排序方式
     *
     * @param sortName
     * @param sortOrder
     * @return
     */
    default Sort getSort(String sortName, SortOrder sortOrder) {
        Sort sort = new Sort();
        sort.setSortOrder(sortOrder);
        sort.setSortName(sortName);
        return sort;
    }
}
