package com.meidianyi.shop.service.pojo.wxapp.medical.im.param;

import lombok.Data;

/**
 * @author 李晓冰
 * @date 2020年08月10日
 */
@Data
public class ImSessionStatusToGoingOnParam {

    private Integer sessionId;
    /**会话上一个状态*/
    private Byte sessionPrevStatus;
}
