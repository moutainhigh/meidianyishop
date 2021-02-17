package com.meidianyi.shop.service.pojo.shop.member.account;

import lombok.Data;

/**
 * @author huangzhuangzhuang
 */
@Data
public class UserIdAndCardIdParam {
	private Integer userId;
	private Integer cardId;
	private Byte getType;
}
