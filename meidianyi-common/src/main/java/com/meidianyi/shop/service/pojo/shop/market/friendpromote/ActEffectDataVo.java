package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 助力活动效果数据
 * @author liangchen
 * @date 2020.03.20
 */
@Data
public class ActEffectDataVo {
    /** 时间 */
    private List<String> date;
    /** 发起用户数 */
    private List<Integer> launch;
    /** 帮助用户数 */
    private List<Integer> promote;
    /** 成功用户数 */
    private List<Integer> success;
    /** 拉新用户数 */
    private List<Integer> newUser;
    /** 发起用户数 */
    private Integer launchTotal;
    /** 帮助用户数 */
    private Integer promoteTotal;
    /** 成功用户数 */
    private Integer successTotal;
    /** 拉新用户数 */
    private Integer newUserTotal;
    /** 开始时间 */
    private Timestamp startTime;
    /** 结束时间 */
    private Timestamp endTime;
}
