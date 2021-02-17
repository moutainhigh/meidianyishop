package com.meidianyi.shop.service.pojo.shop.config.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * The type Bind account param.
 *
 * @author liufei
 * @date 2019 /9/20
 */
@Data
public class BindAccountParam {
    /**
     * The Type. bind表示绑定，unbind表示解除绑定
     */
    @NotBlank
    @JsonProperty(value = "type")
    public String type;
    /**
     * The Biz id. 快递公司客户编码
     */
    @NotBlank
    @JsonProperty(value = "biz_id")
    public String bizId;
    /**
     * The Delivery id. 快递公司ID，参见getAllDelivery
     */
    @NotBlank
    @JsonProperty(value = "delivery_id")
    public String deliveryId;
    /**
     * The Password. 快递公司客户密码
     */
    @NotEmpty
    @JsonProperty(value = "password")
    public String password;
    /**
     * The Remark content. 备注内容（提交EMS审核需要）
     */
    @JsonProperty(value = "remark_content")
    public String remarkContent;
}
