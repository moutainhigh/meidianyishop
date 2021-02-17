package com.meidianyi.shop.service.pojo.shop.overview;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年8月28日 下午2:54:50
 */
@Data
public class BindofficialVo {
	/**
	 * 绑定者昵称
	 */
	private String nickName;
	/**
	 * 是否绑定 1绑定；0 未绑定
	 */
	private Byte isBind;

	private String officialOpenId;
}
