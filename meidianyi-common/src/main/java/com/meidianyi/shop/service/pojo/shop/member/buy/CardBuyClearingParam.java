package com.meidianyi.shop.service.pojo.shop.member.buy;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;

/**
 * 会员卡下单结算
 * @author 孔德成
 * @date 2020/4/9
 */
@Getter
@Setter
@ToString
public class CardBuyClearingParam {

    @NotNull(message = JsonResultMessage.MSG_MEMBER_CARD_ID_EMPTY)
    private Integer cardId;

    private Integer userId;

}
