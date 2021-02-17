package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

/**
 * @Author 常乐
 * @Date 2020-02-26
 */
@Data
public class ShowDistributionGroupParam {
    private String k = "show_distributor_group";
    /**
     * 是否展示分销分组开关 0：展示；1：不展示
     */
    private String v;
}
