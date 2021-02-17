package com.meidianyi.shop.service.pojo.shop.market.firstspecial;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;

/**
 * @author: 王兵兵
 * @create: 2019-08-16 11:19
 **/
@Data
public class FirstSpecialPageListQueryParam {
    /**
     * 活动状态过滤 ：0全部，1进行中，2未开始，3已过期，4已停用
     */
    @Max(4)
    @Min(0)
    private Byte state = (byte)1;

    /**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;

}
