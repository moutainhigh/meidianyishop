package com.meidianyi.shop.service.pojo.shop.market.firstspecial;

import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfig;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-08-16 16:39
 **/
@Data
public class FirstSpecialAddParam {

    /** 活动名称 */
    @NotNull
    private String name;

    /** 活动开始时间 */
    private Timestamp startTime;

    /** 活动结束时间 */
    private Timestamp endTime;

    /** 是否永久有效 */
    @NotNull
    private Byte isForever;

    /** 活动优先级 */
    @NotNull
    @Range(min = 0,max = 100)
    private Byte first;

    /** 限购数量 */
    @NotNull
    private Integer limitAmount;

    /** 改价的商品数组 */
    @NotNull
    @Size(min=1)
    private FirstSpecialGoodsParam[] firstSpecialGoodsParams;

    /** 批量打几折 */
    //private Byte batchDiscount;

    /** 批量减多少 */
    private BigDecimal batchReduce;

    /** 批量折后价 */
    private BigDecimal batchFinalPrice;

    /** 是否批量取整 */
    private Byte isBatchInteger;

    /** 超限购买设置标记，1禁止超限购买，0超限全部恢复原价 */
    private Byte limitFlag;

    /** 分享设置 */
    @NotNull
    private PictorialShareConfig shareConfig;
}
