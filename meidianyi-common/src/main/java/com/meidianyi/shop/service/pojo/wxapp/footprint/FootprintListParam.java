package com.meidianyi.shop.service.pojo.wxapp.footprint;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Getter;
import lombok.Setter;

/**
 * 足迹列表参数
 * @author 孔德成
 * @date 2019/11/5 14:10
 */
@Setter
@Getter
public class FootprintListParam {

    /**
     * 关键字
     */
    private String keyword;

    private Integer userId;

    /**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
