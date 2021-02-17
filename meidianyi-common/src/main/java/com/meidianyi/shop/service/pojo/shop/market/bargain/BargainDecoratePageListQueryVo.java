package com.meidianyi.shop.service.pojo.shop.market.bargain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2020-03-27 18:29
 **/
@Getter
@Setter
public class BargainDecoratePageListQueryVo {
    /**
     * 砍价活动主键
     */
    private Integer id;

    private String bargainName;

    /**
     * 砍价类型0定人1任意价
     */
    private Byte bargainType;
    private Timestamp startTime;
    private Timestamp endTime;
    /**
     * 停用启用的状态：1可用，0停用
     */
    private Byte status;

    /**
     * 砍价活动库存
     */
    private Integer stock;
    /**
     * 当前活动状态：1进行中，2未开始，3已结束，4已停用
     */
    private Byte currentState;


    private Integer goodsId;
    private String goodsName;
    private String goodsImg;
    private BigDecimal shopPrice;

    /**
	 *  指定金额结算模式的砍价底价 或 砍到任意金额结算模式的结算金额上限
	 */
	private BigDecimal expectationPrice;

	/**
	 * 任意金额结算模式的结算金额底价
	 */
	private BigDecimal floorPrice;
}
