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
public class ModuleMap extends ModuleBase {
    /**
     * 省
     */
    @JsonProperty(value = "province")
    private String province="";

    /**
     *
     */
    @JsonProperty(value = "province_code")
    private String provinceCode="";

    /**
     *
     */
    @JsonProperty(value = "city")
    private String city="";

    /**
     *
     */
    @JsonProperty(value = "city_code")
    private String cityCode="";

    /**
     *
     */
    @JsonProperty(value = "area")
    private String area="";

    /**
     *
     */
    @JsonProperty(value = "area_code")
    private String areaCode="";

    /**
     *
     */
    @JsonProperty(value = "address")
    private String address="";

    /**
     *
     */
    @JsonProperty(value = "map_show")
    private Byte mapShow=1;

    /**
     *
     */
    @JsonProperty(value = "latitude")
    private String latitude="";

    /**
     *
     */
    @JsonProperty(value = "longitude")
    private String longitude="";

    /**
     *
     */
    @JsonProperty(value = "img_path")
    private String imgPath="";
}
