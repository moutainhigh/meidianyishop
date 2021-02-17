package com.meidianyi.shop.service.pojo.shop.market;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

/**
 * @author 孔德成
 * @date 2019/7/26 9:54
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MarketSourceUserListParam{

    private String mobile;
    private String userName;
    /**
     * 邀请人
     */
    private String inviteUserName;

    /**
     * 营销活动主键
     */
    @NotNull
    private Integer activityId;

    /**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;

}
