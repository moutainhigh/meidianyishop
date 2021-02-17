/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.Gift;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row13;
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
public class GiftRecord extends UpdatableRecordImpl<GiftRecord> implements Record13<Integer, String, Short, Timestamp, Timestamp, String, String, String, Byte, Byte, Timestamp, Timestamp, Timestamp> {

    private static final long serialVersionUID = -57032192;

    /**
     * Setter for <code>mini_shop_471752.b2c_gift.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_gift.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_gift.name</code>. 活动名称
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_gift.name</code>. 活动名称
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_gift.level</code>. 优先级
     */
    public void setLevel(Short value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_gift.level</code>. 优先级
     */
    public Short getLevel() {
        return (Short) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_gift.start_time</code>. 开始时间
     */
    public void setStartTime(Timestamp value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_gift.start_time</code>. 开始时间
     */
    public Timestamp getStartTime() {
        return (Timestamp) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_gift.end_time</code>. 结束时间
     */
    public void setEndTime(Timestamp value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_gift.end_time</code>. 结束时间
     */
    public Timestamp getEndTime() {
        return (Timestamp) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_gift.goods_id</code>. 活动商品
     */
    public void setGoodsId(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_gift.goods_id</code>. 活动商品
     */
    public String getGoodsId() {
        return (String) get(5);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_gift.rule</code>. 赠品策略
     */
    public void setRule(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_gift.rule</code>. 赠品策略
     */
    public String getRule() {
        return (String) get(6);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_gift.explain</code>. 说明
     */
    public void setExplain(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_gift.explain</code>. 说明
     */
    public String getExplain() {
        return (String) get(7);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_gift.status</code>.
     */
    public void setStatus(Byte value) {
        set(8, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_gift.status</code>.
     */
    public Byte getStatus() {
        return (Byte) get(8);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_gift.del_flag</code>.
     */
    public void setDelFlag(Byte value) {
        set(9, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_gift.del_flag</code>.
     */
    public Byte getDelFlag() {
        return (Byte) get(9);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_gift.del_time</code>. 删除时间
     */
    public void setDelTime(Timestamp value) {
        set(10, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_gift.del_time</code>. 删除时间
     */
    public Timestamp getDelTime() {
        return (Timestamp) get(10);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_gift.create_time</code>.
     */
    public void setCreateTime(Timestamp value) {
        set(11, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_gift.create_time</code>.
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(11);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_gift.update_time</code>. 最后修改时间
     */
    public void setUpdateTime(Timestamp value) {
        set(12, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_gift.update_time</code>. 最后修改时间
     */
    public Timestamp getUpdateTime() {
        return (Timestamp) get(12);
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
    // Record13 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row13<Integer, String, Short, Timestamp, Timestamp, String, String, String, Byte, Byte, Timestamp, Timestamp, Timestamp> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row13<Integer, String, Short, Timestamp, Timestamp, String, String, String, Byte, Byte, Timestamp, Timestamp, Timestamp> valuesRow() {
        return (Row13) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Gift.GIFT.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Gift.GIFT.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field3() {
        return Gift.GIFT.LEVEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field4() {
        return Gift.GIFT.START_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field5() {
        return Gift.GIFT.END_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Gift.GIFT.GOODS_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Gift.GIFT.RULE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Gift.GIFT.EXPLAIN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field9() {
        return Gift.GIFT.STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field10() {
        return Gift.GIFT.DEL_FLAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field11() {
        return Gift.GIFT.DEL_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field12() {
        return Gift.GIFT.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field13() {
        return Gift.GIFT.UPDATE_TIME;
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
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short component3() {
        return getLevel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component4() {
        return getStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component5() {
        return getEndTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getGoodsId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component7() {
        return getRule();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getExplain();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component9() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component10() {
        return getDelFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component11() {
        return getDelTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component12() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component13() {
        return getUpdateTime();
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
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value3() {
        return getLevel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value4() {
        return getStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value5() {
        return getEndTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getGoodsId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getRule();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getExplain();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value9() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value10() {
        return getDelFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value11() {
        return getDelTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value12() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value13() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GiftRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GiftRecord value2(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GiftRecord value3(Short value) {
        setLevel(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GiftRecord value4(Timestamp value) {
        setStartTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GiftRecord value5(Timestamp value) {
        setEndTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GiftRecord value6(String value) {
        setGoodsId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GiftRecord value7(String value) {
        setRule(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GiftRecord value8(String value) {
        setExplain(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GiftRecord value9(Byte value) {
        setStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GiftRecord value10(Byte value) {
        setDelFlag(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GiftRecord value11(Timestamp value) {
        setDelTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GiftRecord value12(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GiftRecord value13(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GiftRecord values(Integer value1, String value2, Short value3, Timestamp value4, Timestamp value5, String value6, String value7, String value8, Byte value9, Byte value10, Timestamp value11, Timestamp value12, Timestamp value13) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached GiftRecord
     */
    public GiftRecord() {
        super(Gift.GIFT);
    }

    /**
     * Create a detached, initialised GiftRecord
     */
    public GiftRecord(Integer id, String name, Short level, Timestamp startTime, Timestamp endTime, String goodsId, String rule, String explain, Byte status, Byte delFlag, Timestamp delTime, Timestamp createTime, Timestamp updateTime) {
        super(Gift.GIFT);

        set(0, id);
        set(1, name);
        set(2, level);
        set(3, startTime);
        set(4, endTime);
        set(5, goodsId);
        set(6, rule);
        set(7, explain);
        set(8, status);
        set(9, delFlag);
        set(10, delTime);
        set(11, createTime);
        set(12, updateTime);
    }
}