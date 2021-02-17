package com.meidianyi.shop.service.pojo.shop.market.coopen;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 孔德成
 * @date 2019/11/22 15:33
 */
@Getter
@Setter
public class CoopenIssueListVo {

    private Integer userId;
    private String username;
    private String mobile;
    private String name;
    private Timestamp receiveTime;
    private String comment;
    private Byte activityAction;
    private String awardName;
}
