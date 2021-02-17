package com.meidianyi.shop.service.pojo.shop.market.form.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 孔德成
 * @date 2020/5/7
 */
@Getter
@Setter
@ToString
public class FormImgItem {

    @JsonProperty("img_url")
    private String imgUrl;
    @JsonProperty("title_link")
    private String titleLink;
    @JsonProperty("src_width")
    private int srcWidth;
    @JsonProperty("src_height")
    private int srcHeight;
    @JsonProperty("can_show")
    private String canShow;
    private boolean showmore;
}
