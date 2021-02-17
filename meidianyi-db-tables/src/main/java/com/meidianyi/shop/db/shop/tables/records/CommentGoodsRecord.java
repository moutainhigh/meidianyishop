/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.CommentGoods;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CommentGoodsRecord extends UpdatableRecordImpl<CommentGoodsRecord> {

    private static final long serialVersionUID = -810212734;

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.shop_id</code>. 店铺id
     */
    public void setShopId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.shop_id</code>. 店铺id
     */
    public Integer getShopId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.user_id</code>. 用户id
     */
    public void setUserId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.user_id</code>. 用户id
     */
    public Integer getUserId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.commstar</code>. 评价星级
     */
    public void setCommstar(Byte value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.commstar</code>. 评价星级
     */
    public Byte getCommstar() {
        return (Byte) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.user_score</code>. 评价可得积分
     */
    public void setUserScore(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.user_score</code>. 评价可得积分
     */
    public Integer getUserScore() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.anonymousflag</code>. 匿名状态 0.未匿名；1.匿名
     */
    public void setAnonymousflag(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.anonymousflag</code>. 匿名状态 0.未匿名；1.匿名
     */
    public Byte getAnonymousflag() {
        return (Byte) get(5);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.commtag</code>. 评价标签
     */
    public void setCommtag(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.commtag</code>. 评价标签
     */
    public String getCommtag() {
        return (String) get(6);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.goods_id</code>. 商品id
     */
    public void setGoodsId(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.goods_id</code>. 商品id
     */
    public Integer getGoodsId() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.order_sn</code>. 订单编号
     */
    public void setOrderSn(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.order_sn</code>. 订单编号
     */
    public String getOrderSn() {
        return (String) get(8);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.comm_note</code>. 评论内容
     */
    public void setCommNote(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.comm_note</code>. 评论内容
     */
    public String getCommNote() {
        return (String) get(9);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.comm_img</code>. 评论图片
     */
    public void setCommImg(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.comm_img</code>. 评论图片
     */
    public String getCommImg() {
        return (String) get(10);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.comment_award_id</code>. 评价有礼活动id
     */
    public void setCommentAwardId(Integer value) {
        set(11, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.comment_award_id</code>. 评价有礼活动id
     */
    public Integer getCommentAwardId() {
        return (Integer) get(11);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.flag</code>. 0:未审批,1:审批通过,2:审批未通过
     */
    public void setFlag(Byte value) {
        set(12, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.flag</code>. 0:未审批,1:审批通过,2:审批未通过
     */
    public Byte getFlag() {
        return (Byte) get(12);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.del_flag</code>. 1:删除
     */
    public void setDelFlag(Byte value) {
        set(13, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.del_flag</code>. 1:删除
     */
    public Byte getDelFlag() {
        return (Byte) get(13);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.is_shop_add</code>. 是否商家增加：0不是，1是
     */
    public void setIsShopAdd(Byte value) {
        set(14, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.is_shop_add</code>. 是否商家增加：0不是，1是
     */
    public Byte getIsShopAdd() {
        return (Byte) get(14);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.bogus_username</code>. 用户名称：商家添加时使用
     */
    public void setBogusUsername(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.bogus_username</code>. 用户名称：商家添加时使用
     */
    public String getBogusUsername() {
        return (String) get(15);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.bogus_user_avatar</code>. 用户头像：商家添加时使用
     */
    public void setBogusUserAvatar(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.bogus_user_avatar</code>. 用户头像：商家添加时使用
     */
    public String getBogusUserAvatar() {
        return (String) get(16);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.create_time</code>.
     */
    public void setCreateTime(Timestamp value) {
        set(17, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.create_time</code>.
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(17);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.update_time</code>. 最后修改时间
     */
    public void setUpdateTime(Timestamp value) {
        set(18, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.update_time</code>. 最后修改时间
     */
    public Timestamp getUpdateTime() {
        return (Timestamp) get(18);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.rec_id</code>. order_goods的rec_id
     */
    public void setRecId(Integer value) {
        set(19, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.rec_id</code>. order_goods的rec_id
     */
    public Integer getRecId() {
        return (Integer) get(19);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.prd_id</code>. 商品规格id
     */
    public void setPrdId(Integer value) {
        set(20, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.prd_id</code>. 商品规格id
     */
    public Integer getPrdId() {
        return (Integer) get(20);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.is_top</code>. 是否置顶
     */
    public void setIsTop(Byte value) {
        set(21, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.is_top</code>. 是否置顶
     */
    public Byte getIsTop() {
        return (Byte) get(21);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.top_time</code>. 置顶时间
     */
    public void setTopTime(Timestamp value) {
        set(22, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.top_time</code>. 置顶时间
     */
    public Timestamp getTopTime() {
        return (Timestamp) get(22);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.is_show</code>. 是否买家秀
     */
    public void setIsShow(Byte value) {
        set(23, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.is_show</code>. 是否买家秀
     */
    public Byte getIsShow() {
        return (Byte) get(23);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_goods.show_time</code>. 买家秀时间
     */
    public void setShowTime(Timestamp value) {
        set(24, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_goods.show_time</code>. 买家秀时间
     */
    public Timestamp getShowTime() {
        return (Timestamp) get(24);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CommentGoodsRecord
     */
    public CommentGoodsRecord() {
        super(CommentGoods.COMMENT_GOODS);
    }

    /**
     * Create a detached, initialised CommentGoodsRecord
     */
    public CommentGoodsRecord(Integer id, Integer shopId, Integer userId, Byte commstar, Integer userScore, Byte anonymousflag, String commtag, Integer goodsId, String orderSn, String commNote, String commImg, Integer commentAwardId, Byte flag, Byte delFlag, Byte isShopAdd, String bogusUsername, String bogusUserAvatar, Timestamp createTime, Timestamp updateTime, Integer recId, Integer prdId, Byte isTop, Timestamp topTime, Byte isShow, Timestamp showTime) {
        super(CommentGoods.COMMENT_GOODS);

        set(0, id);
        set(1, shopId);
        set(2, userId);
        set(3, commstar);
        set(4, userScore);
        set(5, anonymousflag);
        set(6, commtag);
        set(7, goodsId);
        set(8, orderSn);
        set(9, commNote);
        set(10, commImg);
        set(11, commentAwardId);
        set(12, flag);
        set(13, delFlag);
        set(14, isShopAdd);
        set(15, bogusUsername);
        set(16, bogusUserAvatar);
        set(17, createTime);
        set(18, updateTime);
        set(19, recId);
        set(20, prdId);
        set(21, isTop);
        set(22, topTime);
        set(23, isShow);
        set(24, showTime);
    }
}