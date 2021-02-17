package com.meidianyi.shop.service.pojo.wxapp.market.groupbuy;

import lombok.Getter;
import lombok.Setter;

/**
 * 拼团定义
 * @author 孔德成
 * @date 2019/12/11 11:54
 */
@Getter
@Setter
public class GroupBuyDefineInfo {

    private Integer goodsId;


    private Integer limitBuyNum;
    private Integer limitMaxNum;

    /**
     * 拼团id
     */
    private Integer id;
    /**
     * 成团限制
     */
    private Integer limitAmount;
    /**
     * 拼团类型 1 普通拼团 2 老带新
     */
    private Integer activityType;
}
