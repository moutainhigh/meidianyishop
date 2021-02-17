package com.meidianyi.shop.service.pojo.wxapp.medical.im.vo;

import com.meidianyi.shop.service.pojo.wxapp.medical.im.base.ImSessionItemBase;
import lombok.Data;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2020年08月14日
 */
@Data
public class ImSessionPullMsgVo {
    /**会话状态*/
    private Byte status;
    /**会话内容*/
    List<ImSessionItemBase> messages;
}
