package com.meidianyi.shop.service.pojo.shop.market.increasepurchase;

import lombok.Data;

/**
 * @author liufei
 * @date 2019/8/15
 * @description
 */
@Data
public class RedemptionDetailParam {
    private Integer activityId;
    private String nickName;
    private String phoneNumber;
    private Integer redemptionNum;

    private Integer currentPage;
    private Integer pageRows;

    /** 按页导出参数：起始页，结束页，每页行数 */
    private Integer startPage;
    private Integer endPage;
}
