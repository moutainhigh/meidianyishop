package com.meidianyi.shop.service.pojo.shop.goods.deliver;

import com.meidianyi.shop.common.foundation.util.PageResult;

import lombok.Data;
/**
 * @author liangchen
 * @date 2019年8月9日
 */
@Data
public class GoodsDeliverTemplateListVo {
	/** 默认运费模板配置 */
	private String config;
	/** 分页信息 */
	private PageResult<GoodsDeliverTemplateVo> pageResult;
	
	
}
