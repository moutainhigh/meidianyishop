package com.meidianyi.shop.service.pojo.shop.goods.deliver;

import lombok.Data;

/**	运费模板下拉框
 *
 * @author liangchen
 * @date 2019年9月4日
 */
@Data
public class GoodsDeliverBoxVo {
	/** 模板id */
	private Integer deliverTemplateId;
	/** 模板名称 */
	private String templateName;
	/** 模板类型 */
	private String flag;
}
