package com.meidianyi.shop.service.pojo.shop.market.message;


import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息推送通用查询类
 * @author 卢光耀
 * @date 2019-08-09 10:02
 *
*/
@Getter
@Setter
public class MessageTemplateQuery extends BasePageParam {

    private Integer templateId;
    /** 用户ID */
    private Integer userId;
    /** 用户昵称 */
    private String userName;
    /** 用户手机号 */
    private String userMobile;
    /** 是否关注公众号 */
    @JsonProperty("isConcernWP")
    private Boolean isConcernWp;
    /** 消息名称 */
    private String messageName;
    /** 业务标题 */
    private String businessTitle;
    /** 开始日期 */
    private Timestamp startTime ;
    /** 结束日期 */
    private Timestamp endTime ;
    /** 筛选日期 */
    private Integer screeningDate;
    /** 发送类型 */
    private Integer sendType;
    /** 用户是否已点击 */
    private Integer isOnClick;
}
