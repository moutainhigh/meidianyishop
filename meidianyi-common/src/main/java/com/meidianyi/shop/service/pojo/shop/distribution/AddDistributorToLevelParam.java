package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

import java.util.List;

/**
 * @Author 常乐
 * @Date 2020-02-04
 * 分销员等级配置，手动升级添加分销员
 */
@Data
public class AddDistributorToLevelParam {
    /**
     * 等级
     */
    private Byte level;
    /**
     * 升级用户id
     */
    private List<Integer> userIds;
}
