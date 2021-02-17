package com.meidianyi.shop.service.pojo.shop.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author 赵晓东
 * @description
 * @create 2020-08-27 16:10
 **/
@Data
public class AnnounceBo {
    /**
     * 模块名
     */
    @JsonProperty("module_name")
    private String moduleName;
    /**
     * 公告信息
     */
    @JsonProperty("shop_text")
    private String shopText;
    /**
     * 当前公告是否推送给用户
     */
    @JsonProperty("is_pull")
    private Byte isPull;
}
