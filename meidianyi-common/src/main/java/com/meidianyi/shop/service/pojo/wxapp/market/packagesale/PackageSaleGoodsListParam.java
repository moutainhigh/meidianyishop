package com.meidianyi.shop.service.pojo.wxapp.market.packagesale;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

/**
 * @author: 王兵兵
 * @create: 2020-02-20 10:12
 **/
@Getter
@Setter
public class PackageSaleGoodsListParam {

    @NotNull
    private Integer packageId;
    /**  取值1-3 */
    private Byte groupId = 1;

    private String search;

    /**排序方式，1销量，2价格 */
    @Max(2)
    @Min(1)
    private Byte sortName;
    /**排序方式，1desc，2asc */
    @Max(2)
    @Min(1)
    private Byte sortOrder;

    /**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
