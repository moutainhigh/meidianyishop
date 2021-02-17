package com.meidianyi.shop.service.pojo.shop.anchor;

import lombok.Data;

import java.util.List;

/**
 * @author 孔德成
 * @date 2020/9/7 11:24
 */
@Data
public class AnchorParamListVo {

    /**
     * 事件列表
     */
    private String eventList;
    /**
     * 关键词列表
     *
     */
    private List<String> keyList;

    private List<String> vauleList;

}
