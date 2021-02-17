package com.meidianyi.shop.service.pojo.shop.decoration.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author lixinguo
 *
 */
@Setter
@Getter
public class ModuleShopAnnounce extends ModuleBase {
    @JsonProperty("shop_text")
    private String shopText="";

    @JsonProperty("font_color")
    private String fontColor="#333333";

    @JsonProperty("bg_color")
    private String bgColor="#fcf9dd";

    @JsonProperty("title_link")
    private String titleLink="";

    @JsonProperty("announce_position")
    private Byte announcePosition=0;
}
