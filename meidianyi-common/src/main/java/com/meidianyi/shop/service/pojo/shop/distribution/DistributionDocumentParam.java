package com.meidianyi.shop.service.pojo.shop.distribution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author  常乐
 * @Date 2019-12-06
 */
@Data
public class DistributionDocumentParam {
    /**审核开关 0：关闭；1：开启*/
    private Byte activation;
    /**
     * 页面标题
     */
    @JsonProperty(value = "title")
    public String title;

    /**
     * 文案模版
     */
    @JsonProperty(value = "document")
    public String document;
}
