package com.meidianyi.shop.service.pojo.shop.video;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author liangchen
 * @date 2020/5/11
 */
@Data
public class VideoSelectParam {

    /**
     * 视频id
     */
    @NotNull
    Integer videoId;


}
