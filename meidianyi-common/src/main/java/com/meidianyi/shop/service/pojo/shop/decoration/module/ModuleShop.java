package com.meidianyi.shop.service.pojo.shop.decoration.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author lixinguo
 *
 */
@Getter
@Setter
public class ModuleShop extends ModuleBase {
    /***
     *
     */
    @JsonProperty("shop_name")
    private String shopName="";

    /***
     *
     */
    @JsonProperty("shop_notice")
    private String shopNotice="";

    /***
     *
     */
    @JsonProperty("shop_bg_path")
    private String shopBgPath="";

    /***
     *
     */
    @JsonProperty("bg_url")
    private String bgUrl="";
}
