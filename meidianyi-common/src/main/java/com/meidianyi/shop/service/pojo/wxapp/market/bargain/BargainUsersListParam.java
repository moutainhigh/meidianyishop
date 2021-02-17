package com.meidianyi.shop.service.pojo.wxapp.market.bargain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

/**
 * @author: 王兵兵
 * @create: 2019-12-31 13:58
 **/
@Getter
@Setter
public class BargainUsersListParam {
    @NotNull
    private Integer recordId;

    /**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
