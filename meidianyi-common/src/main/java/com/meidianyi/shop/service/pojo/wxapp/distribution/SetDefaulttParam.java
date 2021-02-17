package com.meidianyi.shop.service.pojo.wxapp.distribution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author changle
 * date:2020-06-22
 */
@Data
public class SetDefaulttParam {
    /**推广语id*/
    private Integer languageId;
    /**用户id*/
    private Integer userId;
}
