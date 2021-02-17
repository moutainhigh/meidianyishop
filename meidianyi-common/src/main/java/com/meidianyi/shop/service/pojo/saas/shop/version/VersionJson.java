package com.meidianyi.shop.service.pojo.saas.shop.version;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年7月20日 下午9:00:46
 */
@Data
@NoArgsConstructor
public class VersionJson {
	private String vsName;
	private String name;
	private String enName;
	private String[] includeApi;
}
