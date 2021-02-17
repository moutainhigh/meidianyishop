package com.meidianyi.shop.service.pojo.wxapp.medical.im.param;

import com.meidianyi.shop.common.foundation.util.Page;
import lombok.Getter;
import lombok.Setter;

/**
 * 聊天详情记录分页查询
 * @author 李晓冰
 * @date 2020年08月03日
 */
@Getter
@Setter
public class ImSessionRenderPageParam{
    private Integer sessionId;
    private Boolean isDoctor = false;
    private Boolean isFirstTime = false;
    /**分页开始行下标*/
    private Integer startLineIndex;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
