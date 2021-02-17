package com.meidianyi.shop.service.pojo.shop.decoration.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * 
 * @author lixinguo
 *
 */
@Getter
@Setter
public class ModuleGroupIntegration extends ModuleBase {
    /**活动ID */
    @JsonProperty("act_id")
    private Integer actId=-1;

    /** 1默认标题 ，0自定义标题*/
    @JsonProperty("pin_title")
    private Byte pinTitle=1;

    /**自定义标题*/
    @JsonProperty("pin_title_text")
    private String pinTitleTex="";

    /**隐藏内容：活动内容*/
    @JsonProperty("hide_active")
    private Byte hideActive=0;

    /**隐藏内容：有效期*/
    @JsonProperty("hide_time")
    private Byte hideTime=0;

    /**活动底图 0默认，1自定义*/
    @JsonProperty("module_bg")
    private Byte moduleBg=0;

    /**自定义底图*/
    @JsonProperty("module_img")
    private String moduleIm="";

    /**字体颜色*/
    @JsonProperty("font_color")
    private String fontColor;

    /**  输出属性 */
    @JsonProperty("name")
    private String name;

    @JsonProperty("limit_amount")
    private Short limitAmount;

    @JsonProperty("inte_total")
    private Integer inteTotal;

    @JsonProperty("inte_group")
    private Integer inteGroup;

    @JsonProperty("start_time")
    private Timestamp startTime;

    @JsonProperty("end_time")
    private Timestamp endTime;

    /**
     * 0正常，1活动不存在，2活动已停用，3活动未开始，4活动已结束
     */
    @JsonProperty("can_pin")
    private Byte canPin;
}
