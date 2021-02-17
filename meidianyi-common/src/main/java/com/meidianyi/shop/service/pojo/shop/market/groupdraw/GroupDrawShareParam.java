package com.meidianyi.shop.service.pojo.shop.market.groupdraw;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 活动分享入参
 *
 * @author 郑保乐
 */
@Data
public class GroupDrawShareParam {

    @NotNull
    private Integer groupDrawId;
}
