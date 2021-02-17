package com.meidianyi.shop.service.pojo.wxapp.withdraw;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 分销提现申请参数
 * @author ws
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class WithdrawApplyParam {
    private Integer userId;
    @NotNull(message = JsonResultMessage.DISTRIBUTOR_WITHDRAW_MONEY_NOT_NULL)
    private BigDecimal money;
    @NotNull(message = JsonResultMessage.DISTRIBUTOR_WITHDRAW_REALNAME_NOT_NULL)
    private String realName;
}
