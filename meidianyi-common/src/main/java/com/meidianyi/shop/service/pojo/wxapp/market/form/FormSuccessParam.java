package com.meidianyi.shop.service.pojo.wxapp.market.form;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2020/3/16
 */
@Data
@NoArgsConstructor
public class FormSuccessParam {
    /**
     *
     */
    @NotNull
    private Integer id;
}
