package com.meidianyi.shop.service.pojo.shop.market.message;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 消息推送详情
 * @author 卢光耀
 * @date 2019-09-09 11:57
 *
*/
@Data
public class MessageTemplateDetailVo {
    /** 消息名称 */
    private String name;
    /** 业务标题 */
    private String title;
    /** 消息类型 */
    private Byte action;
    /** 模版内容 */
    private String content;
    /** 页面链接 */
    private String pageLink;
    /** 发送人群 */
    private UserInfoQuery userInfo;
    /** 发送类型 */
    private Byte sendAction;
    /** 开始日期 */
    private Timestamp startTime ;
    /** 结束日期 */
    private Timestamp endTime ;
}
