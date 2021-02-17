package com.meidianyi.shop.service.pojo.wxapp.share;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author liufei
 * @date 3/6/20
 */
@Data
@Builder
public class FormPictorialRule implements Rule {
    @JsonProperty("page_name")
    String pageName;

    @JsonProperty("bg_img")
    String bgImg;
}
