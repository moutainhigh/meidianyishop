package com.meidianyi.shop.service.pojo.shop.config.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liufei
 * @date 2019/7/11
 */
@Data
public class ReturnBusinessAddressParam {
    /**
     * The Consignee.收件人
     */
    @JsonProperty(value = "consignee")
    private String consignee = StringUtils.EMPTY;
    /**
     * The Merchant telephone.收件电话
     */
    @JsonProperty(value = "merchant_telephone")
    private String merchantTelephone = StringUtils.EMPTY;
    /**
     * The Zip code.邮编
     */
    @JsonProperty(value = "zip_code")
    private String zipCode = StringUtils.EMPTY;
    /**
     * The Return address.退货地址
     */
    @JsonProperty(value = "return_address")
    private String returnAddress = StringUtils.EMPTY;
}
