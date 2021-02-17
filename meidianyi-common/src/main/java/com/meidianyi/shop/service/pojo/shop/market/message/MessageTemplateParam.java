package com.meidianyi.shop.service.pojo.shop.market.message;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 营销管理-消息推送传参类
 * @author 卢光耀
 * @date 2019-08-16 09:27
 *
*/
@Data
public class MessageTemplateParam {
    /** 消息名称 */
    private String name;
    /** 业务标题 */
    private String title;
    /** 消息类型 */
    private Byte action;
    /** 模版ID */
    private Integer templateId;
    /** 模版内容 */
    private String content;
    /** 页面链接 */
    private String pageLink;
    /** 发送人群 */
    private UserInfoQuery userInfo;
    /** 发送人群标识 */
    private String userKey;
    /** 发送类型 */
    private Byte sendAction;
    /** 发送状态 */
    private Byte sendStatus;
    /** 开始日期 */
    private Timestamp startTime ;
    /** 结束日期 */
    private Timestamp endTime ;
}
