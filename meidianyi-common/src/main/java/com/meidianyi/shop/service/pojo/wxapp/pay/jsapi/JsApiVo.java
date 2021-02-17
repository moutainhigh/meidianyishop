package com.meidianyi.shop.service.pojo.wxapp.pay.jsapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.wxapp.pay.base.WebPayVo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 王帅
 * 小程序调起支付API参数
 */
@Getter
@Setter
@ToString
@Builder
public class JsApiVo extends WebPayVo {
    private String appId;
    private String timeStamp;
    private String nonceStr;
    /**java关键词转换*/
    @JsonProperty("package")
    private String packageAlias;
    private String signType;
    private String paySign;
}
