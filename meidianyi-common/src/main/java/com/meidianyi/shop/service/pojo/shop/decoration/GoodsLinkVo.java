package com.meidianyi.shop.service.pojo.shop.decoration;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;

/**
 * @author changle
 */
@Data
public class GoodsLinkVo {
	public Integer goodsId;
	public String goodsName;
	public String goodsImg;
	public String goodsSn;
	public String keyWords;
	private Byte goodsType;
	private Byte isCardExclusive;
	public static Integer page;
	
	/**
     * 	分页信息
     */
    public int currentPage = Page.DEFAULT_CURRENT_PAGE;
    public int pageRows = Page.DEFAULT_PAGE_ROWS;
}
