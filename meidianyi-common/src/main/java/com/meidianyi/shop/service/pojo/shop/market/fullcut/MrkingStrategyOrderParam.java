package com.meidianyi.shop.service.pojo.shop.market.fullcut;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

/**
 * @author: 王兵兵
 * @create: 2020-05-09 17:36
 **/
@Getter
@Setter
public class MrkingStrategyOrderParam {

    /**
     * 满折满减活动主键
     */
    @NotNull
    private Integer activityId;

    private String goodsName;
    private String orderSn;
    private Byte[] orderStatus;
    /**
     * 下单人昵称/手机号
     */
    private String userInfo;

    /**
     * 分页信息
     */
    protected Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    protected Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
