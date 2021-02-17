package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

/**
 * @author panjing
 * @data 2020/7/28 18:39
 */
@Data
public class PromoteGoodsConditionParam {
    /**
     * 分销员ID
     */
    private Integer userId;

    /**
     * 是否是个人推广中心
     */
    private Byte isPersonal;
}
