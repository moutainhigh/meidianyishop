package com.meidianyi.shop.service.pojo.shop.applets;

import lombok.Data;


import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/7/11 17:46
 */
@Data
public class AppletsJumpUpdatePrarm {

    @NotNull
    private Integer id;
    @NotNull
    private Integer flag;
}
