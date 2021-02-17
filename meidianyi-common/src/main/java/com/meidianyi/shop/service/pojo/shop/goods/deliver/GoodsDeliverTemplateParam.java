package com.meidianyi.shop.service.pojo.shop.goods.deliver;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author liangchen
 * @date 2019年7月12日
 */

@Data
public class GoodsDeliverTemplateParam {
	/** 运费模板id 修改时必传，添加时不传*/
	private Integer deliverTemplateId;
	/** 运费模板名称 */
	@NotNull
	private String templateName;
	/** 标识 0:普通运费模板,1:重量运费模板 */
	@NotNull
	private Byte flag;
	/** 运费模板具体内容 json字符串 */
    GoodsDeliverTemplateContentParam contentParam;
}
