package com.meidianyi.shop.service.pojo.shop.market.groupdraw.analysis;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

/**
 * 拼团抽奖数据效果
 * @author liangchen
 * @date 2020.01.07
 */
@Data
public class GroupDrawAnalysisMap {
    /** 付款订单数 */
    private Map<String,Integer> orderNumber;
    /** 拉新用户数 */
    private Map<String,Integer> newUser;
    /** 参与用户数 */
    private Map<String,Integer> joinNum;
    /** 成团用户数 */
    private Map<String,Integer> successUserNum;
}
