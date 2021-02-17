package com.meidianyi.shop.service.pojo.shop.member.report;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 用户的商品浏览记录
 * @author 孔德成
 * @date 2020/9/24 9:43
 */
@Data
public class MemberGoodsBrowseReportVo {


    public final static  String GOODSNAME ="goodsName";
    public final static  String SPECIFICATIONS ="specifications";
    public final static  String TIME ="time";
    public final static  String MANUFACTURER ="manufacturer";
    public final static  String PRESCRIPTIONNUM ="prescriptionNum";
    public final static  String ADDCARTNUM ="addCartNum";
    public final static  String COLLECT ="collect";
    public final static  String BUYGOODSNUM ="buyGoodsNum";
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 规格系数
     */
    private String specifications;
    /**
     * 浏览时间
     */
    private Timestamp time;
    /**
     * 生产厂家
     */
    private String manufacturer;
    /**
     * 是否关联处方
     */
    private Integer prescriptionNum;
    /**
     * 添加购物车
     */
    private Integer addCartNum;
    /**
     * 购买过
     */
    private Integer buyGoodsNum;
    /**
     *是否收藏
     */
    private Integer collect;


}
