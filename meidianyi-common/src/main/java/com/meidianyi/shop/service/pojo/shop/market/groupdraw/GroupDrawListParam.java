package com.meidianyi.shop.service.pojo.shop.market.groupdraw;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

/**
 * 拼团抽奖
 *
 * @author 郑保乐
 */
@Getter
@Setter
public class GroupDrawListParam extends BasePageParam {

    /** 活动名称 **/
    private String activityName;
    /** 活动开始时间 **/
    private Timestamp startTime;
    /** 活动结束时间 **/
    private Timestamp endTime;
    /** 活动状态 **/
    private Byte status;
    /** 活动id **/
    private Integer id;
}
