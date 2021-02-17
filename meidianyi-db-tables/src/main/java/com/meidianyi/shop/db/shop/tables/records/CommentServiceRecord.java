/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.CommentService;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record16;
import org.jooq.Row16;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;
import java.sql.Timestamp;


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
public class CommentServiceRecord extends UpdatableRecordImpl<CommentServiceRecord> implements Record16<Integer, Integer, Integer, Integer, Byte, Integer, Byte, String, Integer, String, String, String, Timestamp, Timestamp, Byte, Byte> {

    private static final long serialVersionUID = -313160999;

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.store_id</code>. 门店id
     */
    public void setStoreId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.store_id</code>. 门店id
     */
    public Integer getStoreId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.technician_id</code>. 技师id
     */
    public void setTechnicianId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.technician_id</code>. 技师id
     */
    public Integer getTechnicianId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.user_id</code>. 用户id
     */
    public void setUserId(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.user_id</code>. 用户id
     */
    public Integer getUserId() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.commstar</code>. 评价星级
     */
    public void setCommstar(Byte value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.commstar</code>. 评价星级
     */
    public Byte getCommstar() {
        return (Byte) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.user_score</code>. 评价可得积分
     */
    public void setUserScore(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.user_score</code>. 评价可得积分
     */
    public Integer getUserScore() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.anonymousflag</code>. 匿名状态 0.未匿名；1.匿名
     */
    public void setAnonymousflag(Byte value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.anonymousflag</code>. 匿名状态 0.未匿名；1.匿名
     */
    public Byte getAnonymousflag() {
        return (Byte) get(6);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.commtag</code>. 评价标签
     */
    public void setCommtag(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.commtag</code>. 评价标签
     */
    public String getCommtag() {
        return (String) get(7);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.service_id</code>. 服务id
     */
    public void setServiceId(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.service_id</code>. 服务id
     */
    public Integer getServiceId() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.order_sn</code>. 订单编号
     */
    public void setOrderSn(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.order_sn</code>. 订单编号
     */
    public String getOrderSn() {
        return (String) get(9);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.comm_note</code>. 评论内容
     */
    public void setCommNote(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.comm_note</code>. 评论内容
     */
    public String getCommNote() {
        return (String) get(10);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.comm_img</code>. 评论图片
     */
    public void setCommImg(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.comm_img</code>. 评论图片
     */
    public String getCommImg() {
        return (String) get(11);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.create_time</code>.
     */
    public void setCreateTime(Timestamp value) {
        set(12, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.create_time</code>.
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(12);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.update_time</code>. 最后修改时间
     */
    public void setUpdateTime(Timestamp value) {
        set(13, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.update_time</code>. 最后修改时间
     */
    public Timestamp getUpdateTime() {
        return (Timestamp) get(13);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.flag</code>. 0:未审批,1:审批通过,2:审批未通过
     */
    public void setFlag(Byte value) {
        set(14, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.flag</code>. 0:未审批,1:审批通过,2:审批未通过
     */
    public Byte getFlag() {
        return (Byte) get(14);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_comment_service.del_flag</code>. 1:删除
     */
    public void setDelFlag(Byte value) {
        set(15, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_comment_service.del_flag</code>. 1:删除
     */
    public Byte getDelFlag() {
        return (Byte) get(15);
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
    // Record16 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row16<Integer, Integer, Integer, Integer, Byte, Integer, Byte, String, Integer, String, String, String, Timestamp, Timestamp, Byte, Byte> fieldsRow() {
        return (Row16) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row16<Integer, Integer, Integer, Integer, Byte, Integer, Byte, String, Integer, String, String, String, Timestamp, Timestamp, Byte, Byte> valuesRow() {
        return (Row16) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return CommentService.COMMENT_SERVICE.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return CommentService.COMMENT_SERVICE.STORE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return CommentService.COMMENT_SERVICE.TECHNICIAN_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return CommentService.COMMENT_SERVICE.USER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field5() {
        return CommentService.COMMENT_SERVICE.COMMSTAR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field6() {
        return CommentService.COMMENT_SERVICE.USER_SCORE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field7() {
        return CommentService.COMMENT_SERVICE.ANONYMOUSFLAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return CommentService.COMMENT_SERVICE.COMMTAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field9() {
        return CommentService.COMMENT_SERVICE.SERVICE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return CommentService.COMMENT_SERVICE.ORDER_SN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return CommentService.COMMENT_SERVICE.COMM_NOTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return CommentService.COMMENT_SERVICE.COMM_IMG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field13() {
        return CommentService.COMMENT_SERVICE.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field14() {
        return CommentService.COMMENT_SERVICE.UPDATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field15() {
        return CommentService.COMMENT_SERVICE.FLAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field16() {
        return CommentService.COMMENT_SERVICE.DEL_FLAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component2() {
        return getStoreId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component3() {
        return getTechnicianId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component5() {
        return getCommstar();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component6() {
        return getUserScore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component7() {
        return getAnonymousflag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getCommtag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component9() {
        return getServiceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component10() {
        return getOrderSn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component11() {
        return getCommNote();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component12() {
        return getCommImg();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component13() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component14() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component15() {
        return getFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component16() {
        return getDelFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getStoreId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getTechnicianId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value5() {
        return getCommstar();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value6() {
        return getUserScore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value7() {
        return getAnonymousflag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getCommtag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value9() {
        return getServiceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getOrderSn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getCommNote();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getCommImg();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value13() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value14() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value15() {
        return getFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value16() {
        return getDelFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value2(Integer value) {
        setStoreId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value3(Integer value) {
        setTechnicianId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value4(Integer value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value5(Byte value) {
        setCommstar(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value6(Integer value) {
        setUserScore(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value7(Byte value) {
        setAnonymousflag(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value8(String value) {
        setCommtag(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value9(Integer value) {
        setServiceId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value10(String value) {
        setOrderSn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value11(String value) {
        setCommNote(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value12(String value) {
        setCommImg(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value13(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value14(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value15(Byte value) {
        setFlag(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord value16(Byte value) {
        setDelFlag(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentServiceRecord values(Integer value1, Integer value2, Integer value3, Integer value4, Byte value5, Integer value6, Byte value7, String value8, Integer value9, String value10, String value11, String value12, Timestamp value13, Timestamp value14, Byte value15, Byte value16) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CommentServiceRecord
     */
    public CommentServiceRecord() {
        super(CommentService.COMMENT_SERVICE);
    }

    /**
     * Create a detached, initialised CommentServiceRecord
     */
    public CommentServiceRecord(Integer id, Integer storeId, Integer technicianId, Integer userId, Byte commstar, Integer userScore, Byte anonymousflag, String commtag, Integer serviceId, String orderSn, String commNote, String commImg, Timestamp createTime, Timestamp updateTime, Byte flag, Byte delFlag) {
        super(CommentService.COMMENT_SERVICE);

        set(0, id);
        set(1, storeId);
        set(2, technicianId);
        set(3, userId);
        set(4, commstar);
        set(5, userScore);
        set(6, anonymousflag);
        set(7, commtag);
        set(8, serviceId);
        set(9, orderSn);
        set(10, commNote);
        set(11, commImg);
        set(12, createTime);
        set(13, updateTime);
        set(14, flag);
        set(15, delFlag);
    }
}