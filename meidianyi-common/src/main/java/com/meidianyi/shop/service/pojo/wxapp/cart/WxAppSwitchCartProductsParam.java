package com.meidianyi.shop.service.pojo.wxapp.cart;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 购物车选中状态修改
 * @author 孔德成
 * @date 2019/11/27 10:40
 */
@Getter
@Setter
public class WxAppSwitchCartProductsParam {

    @NotNull
    private List<Integer> cartIds;
    /**
     * 选中状态
     */
    @NotNull
    @Range(min=0, max=1)
    private Byte isChecked;
}
