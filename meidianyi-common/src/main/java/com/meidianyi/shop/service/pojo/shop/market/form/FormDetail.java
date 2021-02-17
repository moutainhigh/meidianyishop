package com.meidianyi.shop.service.pojo.shop.market.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author liufei
 * @date 2019/8/7
 * @description
 */
@Data
public class FormDetail{
    private Integer recId;
    @NotNull
    public Integer pageId;
    @NotNull
    public Integer submitId;
    @NotNull
    public Integer userId;
    public String moduleName;
    public String moduleType;
    public String moduleValue;
    @NotNull
    public String curIdx;
}
