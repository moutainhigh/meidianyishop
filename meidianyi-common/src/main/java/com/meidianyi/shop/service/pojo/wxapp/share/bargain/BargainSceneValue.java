package com.meidianyi.shop.service.pojo.wxapp.share.bargain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.wxapp.share.SceneValueBase;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 李晓冰
 * @date 2020年06月17日
 */
@Getter
@Setter
public class BargainSceneValue extends SceneValueBase {
    @JsonProperty("record_id")
    private Integer recordId;
}
