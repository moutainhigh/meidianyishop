package com.meidianyi.shop.service.pojo.shop.config.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 服务条款配置
 * @author liangchen
 * @date 2020.04.08
 */
@Data
public class ServiceDocumentParam {
    /** 此处直接为html页面格式的字符串 */
    @JsonProperty("service_document")
    private String serviceDocument;
}
