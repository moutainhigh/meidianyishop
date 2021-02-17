package com.meidianyi.shop.service.pojo.shop.goods.es;

import org.elasticsearch.search.sort.SortOrder;


/**
 * @author luguangyao
 */
public class Sort {

    private String sortName;

    private SortOrder sortOrder;

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }
}
