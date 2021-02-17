package com.meidianyi.shop.service.pojo.shop.market.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author liufei
 * @date 2019/8/7
 * @description
 */
@Data
public class FormList{
    private Integer submitId;
    @NotNull
    public Integer pageId;
    @NotNull
    public Integer shopId;
    @NotNull
    public Integer userId;
    public String openId;
    public String nickName;
    public Integer sendScore;
    public String sendCoupons;
}
