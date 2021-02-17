package com.meidianyi.shop.service.pojo.wxapp.market.form;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 小程序表单统计
 * @author 孔德成
 * @date 2020/3/13
 */
@Data
@NoArgsConstructor
public class FormGetParam {

    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 表单id
     */
    @NotNull
    private Integer pageId;
}
