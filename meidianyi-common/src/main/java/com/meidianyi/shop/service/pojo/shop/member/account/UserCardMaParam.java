package com.meidianyi.shop.service.pojo.shop.member.account;

import java.math.BigDecimal;
import java.util.List;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.member.card.ChargeVo;

import lombok.Data;

/**
 * 微信用小程序详情
 * @author zhaojianqiang
 * @time   下午3:32:29
 */
@Data
public class UserCardMaParam {
	private Byte cardType;
	private BigDecimal money;
	private Integer surplus;
	private Integer exchangSurplus;
	private Byte isExchang;
	private PageResult<ChargeVo> chargeList;
	private PageResult<ChargeVo> consumeList;
}
