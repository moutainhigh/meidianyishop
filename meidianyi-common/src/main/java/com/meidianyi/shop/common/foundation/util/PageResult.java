package com.meidianyi.shop.common.foundation.util;

import java.util.List;
/**
 * 
 * @author 新国
 *
 */
public class PageResult<T> {
	public Page page;
	public List<T> dataList;

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "page=" + page +
                ", dataList=" + dataList +
                '}';
    }
}
