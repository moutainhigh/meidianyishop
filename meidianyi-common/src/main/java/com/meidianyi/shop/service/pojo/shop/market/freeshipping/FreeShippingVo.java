package com.meidianyi.shop.service.pojo.shop.market.freeshipping;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author 孔德成
 * @date 2019/7/31 10:46
 */
@Getter
@Setter
public class FreeShippingVo {

    private Integer   id;
    private String    name;
    private Timestamp startTime;
    private Timestamp endTime;
    private Byte      expireType;
    private Byte      status;
    private Byte      currentStatus;
    /**
     * 条件 1全部 0部分
     */
    private Integer   type;
    private Byte      level;
    private String    recommendGoodsId;
    private String    recommendCatId;
    private String    recommendSortId;
    private Timestamp createTime;

    private List<FreeShippingRuleVo> ruleList;


}
