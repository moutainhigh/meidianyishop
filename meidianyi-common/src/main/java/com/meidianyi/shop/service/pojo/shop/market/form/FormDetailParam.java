package com.meidianyi.shop.service.pojo.shop.market.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author liufei
 * @date 2019/8/8
 */
@Data
public class FormDetailParam {
    @NotNull
    private Integer pageId;
}
