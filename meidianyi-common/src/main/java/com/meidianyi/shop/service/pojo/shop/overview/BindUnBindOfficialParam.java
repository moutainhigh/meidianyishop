package com.meidianyi.shop.service.pojo.shop.overview;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author liufei
 * date 2019/7/15
 */
@Data
public class BindUnBindOfficialParam {
    @JsonProperty(value = "account_id")
    private int accountId;
    @JsonProperty(value = "is_bind")
    private Byte isBind;
}
