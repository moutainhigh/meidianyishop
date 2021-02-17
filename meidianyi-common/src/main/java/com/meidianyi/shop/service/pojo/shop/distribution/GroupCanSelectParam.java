package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

/**
 * @Author 常乐
 * @Date 2020-02-25
 */
@Data
public class GroupCanSelectParam {
    /**
     * 分组ID
     */
    private Integer groupId;
    /**
     * 用户是否可选  1：支持；0不支持
     */
    private Byte canSelect;
}
