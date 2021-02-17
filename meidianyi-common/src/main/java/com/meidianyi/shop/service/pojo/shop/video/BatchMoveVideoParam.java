package com.meidianyi.shop.service.pojo.shop.video;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author 孔德成
 * @date 2019/7/9 14:29
 */
@Data
public class BatchMoveVideoParam {

    /**
     * 视频id
     */
    @NotNull
    Integer videoCatId;
    List<@NotNull Integer> videoIds;


}
