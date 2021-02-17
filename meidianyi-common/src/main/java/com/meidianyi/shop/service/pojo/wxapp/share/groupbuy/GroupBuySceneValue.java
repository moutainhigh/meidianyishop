package com.meidianyi.shop.service.pojo.wxapp.share.groupbuy;

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
public class GroupBuySceneValue extends SceneValueBase {
    @JsonProperty("group_id")
    private Integer groupId;
}
