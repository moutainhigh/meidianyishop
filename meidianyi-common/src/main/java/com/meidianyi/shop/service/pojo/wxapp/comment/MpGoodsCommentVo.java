package com.meidianyi.shop.service.pojo.wxapp.comment;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 小程序商品页评价详情
 * @author liangchen
 * @date 2020.03.26
 */
@Data
public class MpGoodsCommentVo {
    /** 评价id */
    private Integer id;
    /** 评价星级 */
    private Byte commstar;
    /** 是否匿名 */
    private Byte anonymousflag;
    /** 评价心得 */
    private String commNote;
    /** 评价图片 */
    private String commImg;
    /** 商品描述 */
    private String goodsAttr;
    /** 用户名 */
    private String username;
    /** 用户头像 */
    private String userAvatar;
    /** 评论时间 */
    private Timestamp createTime;
    /** 商家添加用户名 */
    private String bogusUsername;
    /** 商家添加用户头像 */
    private String bogusUserAvatar;
    /** 规格描述 */
    private String prdDesc;
    /** 回复id */
    private Integer answerId;
    /** 回复内容 */
    private String answer;
    /** 是否置顶 */
    private Byte isTop;
    /** 置顶时间 */
    private Timestamp topTime;
}
