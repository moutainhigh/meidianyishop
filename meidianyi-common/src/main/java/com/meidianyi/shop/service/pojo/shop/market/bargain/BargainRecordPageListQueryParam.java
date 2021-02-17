package com.meidianyi.shop.service.pojo.shop.market.bargain;

import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;

/**
 * @author 王兵兵
 *
 * 2019年7月25日
 */
@Data
public class BargainRecordPageListQueryParam {
	
	@NotNull
	private Integer bargainId;
	
	private String username;
	private String mobile;
	
	/**
	 * 状态过滤 0砍价中，1成功，2失败，-1全部 
	 */
	private Byte status = -1;
	
	/**
	 * 发起时间过滤 
	 */
	private Timestamp startTime;
	private Timestamp endTime;
	
	/**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
