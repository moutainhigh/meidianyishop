package com.meidianyi.shop.service.pojo.wxapp.subscribe;

import java.util.List;

import lombok.Data;

/**
 * 获取小程序需要的模板Id的入参类
 * @author zhaojianqiang
 *
 * 2019年12月5日 下午3:33:59
 */
@Data
public class UpdateTemplateParam {
	private List<TemplateVo> accept;
	private List<TemplateVo> reject;
	/**已被后台封禁 */
	private List<TemplateVo> ban;
}
