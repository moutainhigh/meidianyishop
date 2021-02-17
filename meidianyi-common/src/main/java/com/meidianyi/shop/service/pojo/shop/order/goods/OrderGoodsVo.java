package com.meidianyi.shop.service.pojo.shop.order.goods;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLockField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 
 * @author wangshuai
 */

@Data
@NoArgsConstructor
public class OrderGoodsVo {
	private Integer recId;
	private Integer mainRecId;
	private Integer orderId;
	private String orderSn;
    @RedisLockField
	private Integer goodsId;
	/**货号*/
	private String goodsSn;
	private String goodsName;
	/**退货时为退货数量,发货时为可发货数量*/
	private Integer goodsNumber;
	/**单价*/
	private BigDecimal goodsPrice;
	/**属性（规格）*/
	private String goodsAttr;
	/**SKU*/
	private Integer productId;
	private String goodsImg;
	/**
	 * 以下为发货使用的参数，不参加序列化
	 */
	@JsonIgnore
	private Integer returnNumber;
	private Integer sendNumber;
	/**折后单价*/
	private BigDecimal discountedGoodsPrice;
	
	private String productSn;
	private BigDecimal marketPrice;
	private Integer isGift;
	/**普通商品营销类型*/
	private Byte activityType;
    /**会员专享*/
    private Byte isCardExclusive;
}
