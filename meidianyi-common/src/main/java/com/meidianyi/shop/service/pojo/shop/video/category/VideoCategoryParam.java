package com.meidianyi.shop.service.pojo.shop.video.category;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 孔德成
 * @date 2019/7/15 17:23
 */
@Data
@NoArgsConstructor
public class VideoCategoryParam {


    @NotBlank(message = JsonResultMessage.MSG_VIDEO_CATEGORY_VIDEOCATNAME_NOT_NULL)
    private String videoCatName;
    @NotNull(message = JsonResultMessage.MSG_VIDEO_CATEGORY_VIDEOCATPARENTID_NOT_NULL)
    private Integer videoCatParentId;
    private Integer sort;

}
