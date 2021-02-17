package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

/**
 * @Author 常乐
 * @Date 2020-03-12
 */
@Data
public class UserDistributionVo {
    private Byte distributorLevel;
    private Byte isDistributor;
    private Integer inviteId;
}
