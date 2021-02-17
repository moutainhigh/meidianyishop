package com.meidianyi.shop.service.pojo.shop.market.sharereward;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @author liufei
 * @date 2019/8/19
 */
@Data
public class ShareRewardShowParam {
    /** 分享有礼活动分页展示分模块，所有0，进行中1，未开始2，已过期3，已停用4 ；筛选优先级高于下面的条件*/
    private Byte category;
    /** 活动名称 */
    private String name;
    /** 活动起始时间 */
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd hh:mm:ss")
    private Timestamp startTime;
    /** 活动结束时间 */
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd hh:mm:ss")
    private Timestamp endTime;

    private Integer currentPage;
    private Integer pageRows;
}
