package com.meidianyi.shop.service.pojo.shop.market.givegift.receive;

import lombok.Data;

/**
 * 我要送礼 收礼列表
 *
 * @author 孔德成
 * @date 2019/8/16 10:13
 */
@Data
public class GiveGiftReceiveListParam {
    /** 分页信息 */
    private Integer currentPage;
    private Integer pageRows;
    /**
     * 活动id
     */
    private Integer activityId;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 货号
     */
    private String goodsSn;
    /**
     * 送礼人姓名
     */
    private String giveUserName;
    /**
     * 送礼人电话
     */
    private String giveMobile;
    /**
     * 收礼人姓名
     */
    private String receiveUserName;
    /**
     * 收礼人电话
     */
    private String receiveMobile;
    /**
     * 接收状态
     */
    private Byte orderStatus;
    /**
     * 礼品订单号
     */
    private String mainOrderSn;
    /**
     *
     */
    private Byte returnFinished;


}
