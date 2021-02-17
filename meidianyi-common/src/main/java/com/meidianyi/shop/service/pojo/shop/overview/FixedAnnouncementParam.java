package com.meidianyi.shop.service.pojo.shop.overview;

import lombok.Data;

/**
 * @author liufei
 * date 2019/7/16
 */
@Data
public class FixedAnnouncementParam {
    /** 公告分类 */
    private int categoryId = 1;
    /** 指定获取多少条公告*/
    private int fixedNum = 6;
    /** 排序规则，asc正序，desc逆序，默认逆序 */
    private String orderBy = "desc";
}
