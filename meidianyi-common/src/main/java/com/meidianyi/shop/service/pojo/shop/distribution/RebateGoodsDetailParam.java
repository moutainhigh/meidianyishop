package com.meidianyi.shop.service.pojo.shop.distribution;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 商品返利明细列表入参
 * @author 常乐
 * 2019年8月12日
 */
@Data
public class RebateGoodsDetailParam {
    /**
     * 商品id
     */
    private Integer goodsId;
    /**
     * 分销员手机号
     */
    private String distributorMobile;
    /**
     * 分销员昵称
     */
    private String distributorName;
    /**
     * 下单用户手机号
     */
    private String mobile;
    /**
     * 下单用户昵称
     */
    private String username;
    /**
     * 返利开始时间
     */
    private Timestamp startRebateTime;
    /**
     * 返利截止时间
     */
    private Timestamp endRebateTime;
    /**
     * 返利订单号
     */
    private String rebateOrderSn;
    /**
     * 返利状态
     */
    private Byte rebateStatus;
    /**
     * 分销员真实姓名
     */
    private String distributorRealName;
    /**
     * 返利关系
     */
    private Byte rebateLevel;
    /**
     * 导出条数
     */
    private Integer startNum;
    private Integer endNum;
    /**
     * 	分页信息
     */
    private Integer currentPage;
    private Integer pageRows;
	
}
