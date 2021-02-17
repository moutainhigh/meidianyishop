package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

import java.util.List;

/**
 * @Author 常乐
 * @Date 2020-02-20
 * 分销员设置分组
 */
@Data
public class DistributorSetGroupParam {
    /**
     * 用户ID
     */
    private List userId;
    /**
     * 分销分组id
     */
    private Integer groupId;

}
