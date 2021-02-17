package com.meidianyi.shop.service.pojo.shop.store.goods;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 王兵兵
 *
 * 2019年7月12日
 */
@Data
public class StoreGoodsListQueryVo {
	private String goodsImg;
	private String goodsName;
    /**
     * 平台分类id
     */
	private Integer catId;
    /**
     * 平台分类名称
     */
	private String catName;
	/**
	 * 是否已同步pos,1是已同步
	 */
	private Byte isSync;
	
	/**
	 * 未同步时的规格价格
	 */
	private BigDecimal prdPrice;
	/**
	 * 未同步时的规格库存
	 */
	private Integer prdNumber;
	private String prdDesc;



    private Integer prdId;
	private String prdSn;
	private String prdCodes;

	private String storeName;
	private String goodsCommonName;
	private String goodsApprovalNumber;
	private String goodsProductionEnterprise;
	private String goodsStoreSn;
    private Integer productNumber;
    private BigDecimal productPrice;
    private Byte isOnSale;
}
