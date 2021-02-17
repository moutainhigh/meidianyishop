package com.meidianyi.shop.service.pojo.shop.store.service;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author 王兵兵
 *
 * 2019年7月15日
 */
@Data
public class StoreServiceCategoryListQueryVo {

	private Integer catId;

    /**
	 *  分类名称
	 */
	private String catName;

    private Timestamp createTime;

    /**
     * The Service list.某一服务分类下的所有的服务列表
     */
    private List<StoreServiceListQueryVo> serviceList;
}
