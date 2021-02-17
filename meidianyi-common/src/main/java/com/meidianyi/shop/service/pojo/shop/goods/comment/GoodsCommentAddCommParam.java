package com.meidianyi.shop.service.pojo.shop.goods.comment;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author liangchen
 * @date 2019年07月010日
 */
@Data
public class GoodsCommentAddCommParam {
	/** 商品id */
	private Integer goodsId;
    /** 规格id */
    private Integer prdId;
	/** 用户名 */
	private String bogusUsername;
    /** 用户头像 */
	private String bogusUserAvatar;
    /** 评论时间 */
	private Timestamp createTime;
    /** 评价星级 */
	private Byte commstar;
    /** 评价内容 */
	private String commNote;
    /** 是否匿名 */
	private Byte anonymousFlag;
    /** 评论图片 */
	private String commImg;
    
}
