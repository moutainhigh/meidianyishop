package com.meidianyi.shop.service.pojo.shop.auth;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author chenjie
 * @date 2020年08月20日
 */
@Data
@NoArgsConstructor
public class StoreLoginParam {
    @NotBlank(message = JsonResultMessage.MSG_ACCOUNT_NAME_NOT_NULL)
    public String username;
    public String storeUsername;
    public String password;

    /**
     * 门店账号类型：1店员，2店长
     */
    @NotNull(message = JsonResultMessage.MSG_ACCOUNT_ISSUBLOGIN_NOT_NULL)
    public Byte storeAccountType=2;
    public String storeNo;
    public Integer sysId;
}
