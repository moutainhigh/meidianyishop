package com.meidianyi.shop.service.shop.recommend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 好物圈相关接口
 * @author zhaojianqiang
 *
 * 2019年11月18日 上午10:39:53
 */
@Service
public class RecommendService {
	/**
	 * 收藏
	 */
	@Autowired
	public CollectionMallService collectionMallService;
	/**
	 * 商品 小程序页面发送
	 */
	@Autowired
	public GoodsMallService goodsMallService;
	
	/**
	 * 订单
	 */
	@Autowired
	public OrderMallService orderMallService;
	
	/**
	 * 物品信息
	 */
	@Autowired
	public ProductMallService productMallService;

}
