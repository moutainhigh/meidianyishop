package com.meidianyi.shop.service.pojo.shop.market.givegift.record;

import lombok.Data;

/**
 * 我要送礼 收礼列表查询
 *
 * @author 孔德成
 * @date 2019/8/16 10:10
 */
@Data
public class GiveGiftRecordListParam {

    /** 分页信息 */
    private Integer currentPage;
    private Integer pageRows;

    /**
     * 送礼人,姓名
     */
    private String userName;

    /**
     * 送礼人手机号
     */
    private String mobile;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 货号
     */
    private String goodsSn;

    /**
     * 活动ID
     */
    private Integer activityId;

    /**
     * 订单状态
     */
    private Byte orderStatus;
}
