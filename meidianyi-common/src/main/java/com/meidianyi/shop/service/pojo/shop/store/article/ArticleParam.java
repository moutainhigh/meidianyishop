package com.meidianyi.shop.service.pojo.shop.store.article;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;

/**
 * 门店公告
 * @author liangchen
 * @date 2020.03.31
 */
@Data
public class ArticleParam {

    /** 标题 */
    private String title;
    /** 发布状态 */
    private Byte status = ALL_STATUS;
    public static final Byte ALL_STATUS = -1;
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
