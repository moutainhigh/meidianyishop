package com.meidianyi.shop.service.pojo.shop.market.groupbuy.param;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/7/19 16:16
 */
@Data
public class GroupBuyIdParam {

    @NotNull
    private Integer id;
    @NotNull(groups = GroupBuyStatusVaild.class)
    @Range(max = 1,min = 0)
    private Byte status;
}
