package com.meidianyi.shop.service.pojo.shop.market.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * @author liufei
 * @date 2019/8/9
 */
@Data
public class FeedBackDetailVo {
    @JsonIgnore
    private Integer submitId;
    private String moduleName;
    private String moduleType;
    /**
     * moduleValue为用户填写的值，或者是用户选中选项所表示的具体值，而不是标识
     */
    private String moduleValue;
    /** 当模块为选项类型时，选项所包含的所有值，包括用户所选的 */
    private List<String> moduleValueList;
    @JsonIgnore
    private String curIdx;

}
