package com.meidianyi.shop.service.pojo.shop.market.message;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 推送人群自定义规则
 * @author 卢光耀
 * @date 2019-08-09 11:16
 *
*/
@Data
public class CustomRuleInfo {
    /** N天内没有交易记录 */
    private Integer noPayDay;
    /** N天内有交易记录 */
    private Integer payedDay;
    /** 购买次数大于N */
    private Integer buyTimesMore;
    /** 购买次数小于N */
    private Integer buyTimesLess;
    /** 购买均价大于N */
    private BigDecimal moneyAvgMore;
    /** 购买均价小于N */
    private BigDecimal moneyAvgLess;
    /** 登陆周期开始日期 */
    private Timestamp loginStart;
    /** 登陆周期截止日期 */
    private Timestamp loginEnd;
}
