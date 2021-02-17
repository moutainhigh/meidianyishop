package com.meidianyi.shop.service.pojo.shop.market.packagesale;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.meidianyi.shop.service.pojo.saas.category.SysCatevo;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsView;

import com.meidianyi.shop.service.pojo.shop.goods.sort.GoodsSortSelectListVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月13日
 * 打包一口价 定义信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackSaleDefineVo {
	/** 活动ID */
	private Integer id;
	/** 活动名称 */
	private String packageName;
	/** 开始时间 */
	private Timestamp startTime;
	/** 结束时间 */
	private Timestamp endTime;
	/**活动类型**/
	private Byte packageType;
	/** 结算总价格 */
	private BigDecimal totalMoney;
	/**折扣比例**/
	private BigDecimal totalRatio;
	/** 商品组1 */
	private GoodsGroupVo group1;
	/** 商品组2 */
	private GoodsGroupVo group2;
	/** 商品组3 */
	private GoodsGroupVo group3;
	
	
	
	
	
	@Data
	@NoArgsConstructor
	public class GoodsGroupVo{
		
		/** 商品组1名称 */
		@NotBlank
		private String groupName;
		/** 至少需要选择件数 */
		@NotNull
		private Integer goodsNumber;
		/** 商品ID列表 */
		private List<GoodsView> goodsList;
		/** 平台分类ID列表 */
		private List<Integer> catIdList;
		/**平台分类vo列表*/
		private List<SysCatevo> cateVoList;
		/** 商家分类ID列表 */
		private List<Integer> sortIdList;
		/** 商家分类vo列表 */
		private List<GoodsSortSelectListVo> sortVoList;
	}
}

