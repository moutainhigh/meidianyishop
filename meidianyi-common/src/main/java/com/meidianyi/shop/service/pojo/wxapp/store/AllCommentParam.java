package com.meidianyi.shop.service.pojo.wxapp.store;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2020-02-11 15:29
 **/
@Getter
@Setter
public class AllCommentParam {
    @NotNull
    private Integer serviceId;

    /**筛选评价类型，0全部，1好评，2中评，3差评**/
    private Byte type = 0;
}
