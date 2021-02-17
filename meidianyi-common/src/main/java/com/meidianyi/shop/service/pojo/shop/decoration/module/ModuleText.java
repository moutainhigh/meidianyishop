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
public class ModuleText extends ModuleBase {
    @JsonProperty("title")
    private String title="";

    @JsonProperty("fonts_size")
    private Integer fontsSize=1;

    @JsonProperty("fonts_color")
    private String fontsColor="#333333";

    @JsonProperty("bgs_color")
    private String bgsColor="#ffffff";

    @JsonProperty("title_link")
    private String titleLink="";

    @JsonProperty("show_pos")
    private Byte showPos=1;

}
