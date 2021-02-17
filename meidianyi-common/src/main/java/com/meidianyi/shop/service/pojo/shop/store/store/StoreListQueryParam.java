package com.meidianyi.shop.service.pojo.shop.store.store;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 王兵兵
 *
 * 2019年7月4日
 */
@Data
@NoArgsConstructor
public class StoreListQueryParam {
	private String groupName;
	private Integer groupId;
	private Boolean isAuthPos;
	/**
	 *  门店名称/编码/负责人
	 */
	private String keywords;
    /**营业状态1:营业，0:关店*/
    private Byte businessState;
    /** The Auto pick.是否自提设置，0否，1是 */
    private Short autoPick;
    /** 是否支持同城配送 0否 1支持 */
    private Byte cityService;
    private String manager;
	/**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
    private List<Integer> storeIds;
}
