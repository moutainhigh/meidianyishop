package com.meidianyi.shop.service.pojo.shop.market.seckill;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.meidianyi.shop.common.foundation.util.Page;

/**
 * @author 王兵兵
 *
 * 2019年7月24日
 */
@Data
public class SeckillPageListQueryParam {

    /**
     *    活动状态过滤 ：1进行中，2未开始，3已过期，4已停用
     */
    private Byte[] state;

    /**
     * 	过滤选项：goodsName或秒杀活动名
     */
    private String keywords;
	
	/**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
