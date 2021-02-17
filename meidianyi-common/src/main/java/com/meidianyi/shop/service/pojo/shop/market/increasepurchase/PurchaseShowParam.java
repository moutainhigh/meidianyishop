package com.meidianyi.shop.service.pojo.shop.market.increasepurchase;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author liufei
 * @date 2019/8/14
 * @description 分页查询入参
 */
@Data
public class PurchaseShowParam {
    /** 加价购页面分页展示分模块，进行中1 ，未开始2，已过期3，已停用4，所有0 ；筛选优先级高于下面的条件*/
    private Byte category;
    /** 活动名称 */
    private String name;
    /** 活动起始时间 */
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd hh:mm:ss")
    private Timestamp startTime;
    /** 活动结束时间 */
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd hh:mm:ss")
    private Timestamp endTime;
    /** 加价购条件上下限，满多少可以参与活动 */
    private BigDecimal fullPriceUp;
    private BigDecimal fullPriceDown;
    /** 换购条件上下限，加购多少钱可以某一具体活动 */
    private BigDecimal purchasePriceUp;
    private BigDecimal purchasePriceDown;
    /** 状态 1: 启用 0:禁用' */
    private Byte status;
    private Integer currentPage;
    private Integer pageRows;
}
