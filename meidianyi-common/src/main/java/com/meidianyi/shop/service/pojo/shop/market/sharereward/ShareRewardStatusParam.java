package com.meidianyi.shop.service.pojo.shop.market.sharereward;

import lombok.Data;

/**
 * @author liufei
 * @date 2019/8/20
 * @description
 */
@Data
public class ShareRewardStatusParam {
    private Integer shareId;
    /** 1停用，0启用，2删除 */
    private Byte status;
}
