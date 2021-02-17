package com.meidianyi.shop.service.pojo.wxapp.collection;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 商品收藏出参类
 * @author 常乐
 * 2019年10月16日
 */
@Data
public class CollectListVo {
	/**
	 * id
	 */
	private Integer id;
    /**
     * 商品id
     */
	private Integer goodsId;
	/**
	 * 用户ID
	 */
	private Integer userId;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 商品价格
	 */
	private BigDecimal shopPrice;
	/**
	 * 商品图片
	 */
	private String goodsImg;	
	/**
	 * 商品类型
	 */
	private Integer goodsType;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 收藏时商品价格
	 */
	private BigDecimal collectPrice;
	/**
	 * 收藏时间
	 */
	private Timestamp createTime;
    /**
     * 拼团价
     */
	private BigDecimal groupPrice;
	
}
