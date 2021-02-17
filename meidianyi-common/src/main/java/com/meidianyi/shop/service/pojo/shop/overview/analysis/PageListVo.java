package com.meidianyi.shop.service.pojo.shop.overview.analysis;

import com.meidianyi.shop.service.foundation.util.I18N;
import lombok.Data;

/**
 * 
 * @author liangchen
 * @date  2019年7月16日
 */
@Data
public class PageListVo {
	/** 页面路径 */
	private String pagePath;
    /** 页面名称 */
	@I18N(propertiesFileName = "page")
	private String pageName;
    /** 访问次数 */
	private Integer pageVisitPv;
    /** 访问数占比 */
	private Double rate;
	
}
