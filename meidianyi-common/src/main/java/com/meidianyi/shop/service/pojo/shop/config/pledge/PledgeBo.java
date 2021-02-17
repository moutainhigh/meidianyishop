package com.meidianyi.shop.service.pojo.shop.config.pledge;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 服务配置业务数据
 * @author 孔德成
 * @date 2019/11/19 18:04
 */
@Getter
@Setter
public class PledgeBo {
    /**
     * 是否`开启
     */
    private String pledgeSwitch;

    private List<PledgeInfo> pledgeList;
}
