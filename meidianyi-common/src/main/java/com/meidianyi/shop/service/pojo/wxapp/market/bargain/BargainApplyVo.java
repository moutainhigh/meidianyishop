package com.meidianyi.shop.service.pojo.wxapp.market.bargain;

import lombok.Builder;
import lombok.Data;

/**
 * @author: 王兵兵
 * @create: 2019-12-27 14:57
 **/
@Data
@Builder
public class BargainApplyVo {
    /**
     * 返回状态码
     * -1操作失败，0正常返回，
     *  1该活动不存在  2该活动已停用 3该活动未开始 4该活动已结束 5商品库存不足
     */
    private Byte resultCode;
    /**
     * 发起记录的ID
     */
    private Integer recordId;
}
