package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 积分兑换分页查询列表
 * @author liangchen
 * @date 2019年8月14日
 */
@Data
public class IntegralConvertListVo {
	/** 活动id */
	private Integer id;
	/** 活动名称 */
	private String name;
	/** 商品id */
	private Integer goodsId;
	/** 商品图片 */
	private String goodsImg;
	/** 商品名称 */
	private String goodsName;
	/** 开始时间 */
	private Timestamp startTime;
	/** 结束时间 */
	private Timestamp endTime;
	/** 兑换所需现金 */
	private BigDecimal money;
	/** 兑换所需积分 */
	private Integer score;
	/** 商品库存 */
	private Integer goodsNumber;
	/** 积分兑换库存 */
	private Integer stock;
	/** 已兑换数量 */
	private Integer number;
    /** 兑换用户数 */
    private Integer userNumber;
	/** 状态 1正常 0停用 */
	private Byte status;
    /** 删除 */
    private Byte delFlag;
    /** 0：全部活动 1：进行中 2：未开始 3：已过期 4：已停用*/
    private Integer actStatus;
}
