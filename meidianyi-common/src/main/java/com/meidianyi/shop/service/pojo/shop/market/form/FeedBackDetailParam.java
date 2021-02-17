package com.meidianyi.shop.service.pojo.shop.market.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author liufei
 * @date 2019/8/9
 */
@Data
public class FeedBackDetailParam {
    @NotNull
    private Integer pageId;
    @NotNull
    private Integer submitId;
    @NotNull
    private Integer userId;
}
