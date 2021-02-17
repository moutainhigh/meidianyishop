package com.meidianyi.shop.service.pojo.shop.goods.deliver;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author liangchen
 * @date 2019年7月12日
 */
@Data
public class GoodsDeliverTemplateVo {
	/** 模板id */
	private Integer deliverTemplateId;
    /** 模板名称 */
	private String templateName;
    /** 标识符 0：普通模板 1：重量模板 */
    private Byte flag;
    /** 字符串-模板内容 */
    @JsonIgnore
	private String templateContent;
    /** 对象格式-模板内容 */
	private GoodsDeliverTemplateContentParam content;
}
