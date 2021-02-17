package com.meidianyi.shop.service.pojo.shop.member.card;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年8月7日
* @Description: 会员卡id入参
*/
@Data
public class CardIdParam {
	@NotNull(message = JsonResultMessage.MSG_MEMBER_CARD_ID_EMPTY)
	private Integer id;
}
