package com.meidianyi.shop.service.pojo.shop.store.goods;

import com.meidianyi.shop.common.foundation.util.Page;
import lombok.Data;

import java.util.List;


/**
 * @author 王兵兵
 *
 * 2019年7月12日
 */
@Data
public class StoreGoodsListQueryParam {
	
	private Integer catId;
	private Byte isOnSale;
	
	/**
     * 是否同步pos  0未同步，1同步
     */
	private Byte isSync;
	
	private String keywords;

	private Integer storeId;

	/**用户有权限的门店id集合*/
	private List<Integer> limitedStoreIds;

	private Byte isSaleOut;
	/**
     * 	分页信息
     */
    private int currentPage = Page.DEFAULT_CURRENT_PAGE;
    private int pageRows = Page.DEFAULT_PAGE_ROWS;
}
