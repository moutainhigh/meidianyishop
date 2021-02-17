package com.meidianyi.shop.service.pojo.shop.market.bargain.analysis;

import lombok.Data;

/**
 * @description: 砍价效果数据-各项总数
 * @author: 王兵兵
 * @create: 2019-08-01 09:41
 **/
@Data
public class BargainAnalysisTotalVo {
    /** 发起砍价用户数 */
    private Integer recordTotal;
    /** 帮砍人次数 */
    private Integer userTotal;
    /** 活动订单数 */
    private Integer orderTotal;
    /** 拉新用户数 */
    private Integer sourceTotal;
}
