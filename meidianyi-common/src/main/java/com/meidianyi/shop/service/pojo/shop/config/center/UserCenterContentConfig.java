package com.meidianyi.shop.service.pojo.shop.config.center;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.validator.CharacterValid;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 孔德成
 * @date 2019/7/11 10:55
 */
@Data
@NoArgsConstructor
public class UserCenterContentConfig {

    private String title;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "icon_name")
    private String iconName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "is_show")
    private String isShow;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String icon;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String link;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "link_name")
    private String linkName;


}
