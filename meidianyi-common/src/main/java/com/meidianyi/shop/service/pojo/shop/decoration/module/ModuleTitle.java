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
public class ModuleTitle extends ModuleBase {
    @JsonProperty("title")
    private String title="";

    @JsonProperty("title_model")
    private Byte titleModel=1;

    @JsonProperty("title_link")
    private String titleLink="";

    @JsonProperty("tit_center")
    private Byte titCenter=1;

    @JsonProperty("color")
    private String color="#333333";

    @JsonProperty("bg_color")
    private String bgColor="#ffffff";

    @JsonProperty("title_date")
    private String titleDate="";

    @JsonProperty("title_author")
    private String titleAuthor="";

    @JsonProperty("link_title")
    private String linkTitle="";

    @JsonProperty("img_url")
    private String imgUrl="";
}
