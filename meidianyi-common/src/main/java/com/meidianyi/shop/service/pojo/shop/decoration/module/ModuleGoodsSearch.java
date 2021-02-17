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
public class ModuleGoodsSearch extends ModuleBase {

    /**
     *
     */
    @JsonProperty("list_style")
    private Byte searchStyle=1;

    /**
     *
     */
    @JsonProperty("search_font")
    private Byte searchFont=1;

    /**
     *
     */
    @JsonProperty("box_color")
    private String boxColo="#eee";

    /**
     *
     */
    @JsonProperty("back_color")
    private String backColor="#fff";

    /**
     *
     */
    @JsonProperty("search_sort")
    private Byte searchSort=0;

    /**
     *
     */
    @JsonProperty("sort_bg_color")
    private String sortBgColor="#666666";

    /**
     *
     */
    @JsonProperty("search_position")
    private Byte searchPosition=0;
}
