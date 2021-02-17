package com.meidianyi.shop.service.pojo.shop.goods.comment;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author liangchen
 * @date 2019年07月07日
 */
@Data
public class GoodsCommentVo {
	/** 评论id */
	private Integer id;
    /** 订单编号 */
    private String orderSn;
    /** 评价星级 */
    private Byte commstar;
    /** 评价内容 */
    private String commNote;
    /** 评价图片 */
    private String commImg;
    /** 评价回复 */
    private String content;
    /** 评价时间 */
    private Timestamp createTime;
    /** 商品名称 */
    private String goodsName;
    /** 商品图片 */
    private String goodsImg;
    /** 用户id */
    private Integer userId;
    /** 评论用户名 */
    private String username;
    /** 评论用户手机号 */
    private String mobile;
    /** 是否匿名评价 0：否，1：是 */
    private Byte anonymousflag;
    /** 评价有礼活动id */
    private Integer commentAwardId;
    /** 奖励类型 1积分 2优惠卷 3 余额 4幸运大抽奖 5神秘奖励 */
    private Integer awardType;
    /** 积分值 */
    private Integer score;
    /** 优惠券名称 */
    private String actName;
    /** 余额值 */
    private BigDecimal account;
    /** 用户名称：商家添加时使用 */
    private String bogusUsername;
    /** 审核状态 0待审核 1已通过 2已拒绝 */
    private Byte flag;
    /** 规格描述 */
    private String prdDesc;
    /** 是否置顶 */
    private Byte isTop;
    /** 是否买家秀 */
    private Byte isShow;
}
