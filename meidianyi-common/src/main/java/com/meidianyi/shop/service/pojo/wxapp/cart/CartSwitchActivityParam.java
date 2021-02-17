package com.meidianyi.shop.service.pojo.wxapp.cart;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 购物车切换活动
 * @author 孔德成
 * @date 2020/2/24
 */
@Getter
@Setter
public class CartSwitchActivityParam {

    @NotNull
    private List<Integer> cartIds;
    /**
     * 选中状态
     */
    @NotNull
    private Byte isChecked;

    /**
     * 活动id
     */
    @NotNull
    private Integer activityId;
    /**
     * 活动type
     */
    @NotNull
    private Byte activityType;

}
