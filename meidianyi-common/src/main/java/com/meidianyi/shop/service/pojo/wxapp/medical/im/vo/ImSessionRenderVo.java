package com.meidianyi.shop.service.pojo.wxapp.medical.im.vo;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 会话框初始化渲染类
 * @author 李晓冰
 * @date 2020年07月21日
 */
@Data
public class ImSessionRenderVo {
    /**本次会话截止时间*/
    private Timestamp limitTime;
    /**过往消息列表*/
    private List<ImSessionItemRenderVo> sessionItemRenderVos;
}
