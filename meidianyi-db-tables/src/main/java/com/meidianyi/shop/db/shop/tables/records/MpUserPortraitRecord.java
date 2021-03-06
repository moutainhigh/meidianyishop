/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.MpUserPortrait;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
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
public class MpUserPortraitRecord extends UpdatableRecordImpl<MpUserPortraitRecord> implements Record8<Integer, String, String, String, Byte, Timestamp, Timestamp, Timestamp> {

    private static final long serialVersionUID = -1623165670;

    /**
     * Setter for <code>mini_shop_8984736.b2c_mp_user_portrait.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_8984736.b2c_mp_user_portrait.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_8984736.b2c_mp_user_portrait.ref_date</code>. 时间： 如： "20180313"
     */
    public void setRefDate(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_8984736.b2c_mp_user_portrait.ref_date</code>. 时间： 如： "20180313"
     */
    public String getRefDate() {
        return (String) get(1);
    }

    /**
     * Setter for <code>mini_shop_8984736.b2c_mp_user_portrait.visit_uv_new</code>. 新用户
     */
    public void setVisitUvNew(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_8984736.b2c_mp_user_portrait.visit_uv_new</code>. 新用户
     */
    public String getVisitUvNew() {
        return (String) get(2);
    }

    /**
     * Setter for <code>mini_shop_8984736.b2c_mp_user_portrait.visit_uv</code>. 活跃用户
     */
    public void setVisitUv(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_8984736.b2c_mp_user_portrait.visit_uv</code>. 活跃用户
     */
    public String getVisitUv() {
        return (String) get(3);
    }

    /**
     * Setter for <code>mini_shop_8984736.b2c_mp_user_portrait.type</code>. 0:昨天，1：最近7天，2:30天
     */
    public void setType(Byte value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_8984736.b2c_mp_user_portrait.type</code>. 0:昨天，1：最近7天，2:30天
     */
    public Byte getType() {
        return (Byte) get(4);
    }

    /**
     * Setter for <code>mini_shop_8984736.b2c_mp_user_portrait.create_time</code>.
     */
    public void setCreateTime(Timestamp value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_8984736.b2c_mp_user_portrait.create_time</code>.
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(5);
    }

    /**
     * Setter for <code>mini_shop_8984736.b2c_mp_user_portrait.update_time</code>. 最后修改时间
     */
    public void setUpdateTime(Timestamp value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_8984736.b2c_mp_user_portrait.update_time</code>. 最后修改时间
     */
    public Timestamp getUpdateTime() {
        return (Timestamp) get(6);
    }

    /**
     * Setter for <code>mini_shop_8984736.b2c_mp_user_portrait.start_time</code>. 开始时间，ref_date前8个字符
     */
    public void setStartTime(Timestamp value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_8984736.b2c_mp_user_portrait.start_time</code>. 开始时间，ref_date前8个字符
     */
    public Timestamp getStartTime() {
        return (Timestamp) get(7);
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
    // Record8 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<Integer, String, String, String, Byte, Timestamp, Timestamp, Timestamp> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<Integer, String, String, String, Byte, Timestamp, Timestamp, Timestamp> valuesRow() {
        return (Row8) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return MpUserPortrait.MP_USER_PORTRAIT.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return MpUserPortrait.MP_USER_PORTRAIT.REF_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return MpUserPortrait.MP_USER_PORTRAIT.VISIT_UV_NEW;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return MpUserPortrait.MP_USER_PORTRAIT.VISIT_UV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field5() {
        return MpUserPortrait.MP_USER_PORTRAIT.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field6() {
        return MpUserPortrait.MP_USER_PORTRAIT.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field7() {
        return MpUserPortrait.MP_USER_PORTRAIT.UPDATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field8() {
        return MpUserPortrait.MP_USER_PORTRAIT.START_TIME;
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
    public String component2() {
        return getRefDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getVisitUvNew();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getVisitUv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component5() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component6() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component7() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component8() {
        return getStartTime();
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
    public String value2() {
        return getRefDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getVisitUvNew();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getVisitUv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value5() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value6() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value7() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value8() {
        return getStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpUserPortraitRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpUserPortraitRecord value2(String value) {
        setRefDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpUserPortraitRecord value3(String value) {
        setVisitUvNew(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpUserPortraitRecord value4(String value) {
        setVisitUv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpUserPortraitRecord value5(Byte value) {
        setType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpUserPortraitRecord value6(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpUserPortraitRecord value7(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpUserPortraitRecord value8(Timestamp value) {
        setStartTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpUserPortraitRecord values(Integer value1, String value2, String value3, String value4, Byte value5, Timestamp value6, Timestamp value7, Timestamp value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MpUserPortraitRecord
     */
    public MpUserPortraitRecord() {
        super(MpUserPortrait.MP_USER_PORTRAIT);
    }

    /**
     * Create a detached, initialised MpUserPortraitRecord
     */
    public MpUserPortraitRecord(Integer id, String refDate, String visitUvNew, String visitUv, Byte type, Timestamp createTime, Timestamp updateTime, Timestamp startTime) {
        super(MpUserPortrait.MP_USER_PORTRAIT);

        set(0, id);
        set(1, refDate);
        set(2, visitUvNew);
        set(3, visitUv);
        set(4, type);
        set(5, createTime);
        set(6, updateTime);
        set(7, startTime);
    }
}
