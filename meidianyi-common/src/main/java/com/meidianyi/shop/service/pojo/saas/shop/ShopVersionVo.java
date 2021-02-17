
package com.meidianyi.shop.service.pojo.saas.shop;

import java.sql.Timestamp;

import com.meidianyi.shop.service.pojo.saas.shop.version.VersionConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 
 * @author zhaojianqiang
 *
 * 2019年11月14日 上午10:29:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopVersionVo {
	private Integer id;
	private String versionName;
	private String level;
	private VersionConfig content;
	private Timestamp created;
	private Timestamp updateTime;
	private String desc;
	private Byte flag;
}
