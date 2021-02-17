package com.meidianyi.shop.service.pojo.shop.distribution.withdraw;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 分销提现审核参数
 * @author ws
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class WithdrawAuditParam {
    @NotNull(message = JsonResultMessage.DISTRIBUTOR_WITHDRAW_ISPASS_NOT_NULL)
    private Byte isPass;
    @NotNull(message = JsonResultMessage.DISTRIBUTOR_WITHDRAW_ORDER_SN_NOT_NULL)
    private String orderSn;
    private String clientIp;
    private String refuseDesc;
}
