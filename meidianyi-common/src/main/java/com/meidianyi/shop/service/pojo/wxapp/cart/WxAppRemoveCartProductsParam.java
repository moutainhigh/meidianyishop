package com.meidianyi.shop.service.pojo.wxapp.cart;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.validator.ListValid;

import java.util.List;

/**
 * @author 孔德成
 * @date 2019/11/8 16:30
 */
@Getter
@Setter
public class WxAppRemoveCartProductsParam {


    @ListValid(min = 1)
    private List<Integer> cartIds;
}
