/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.FirstSpecial;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record18;
import org.jooq.Row18;
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
public class FirstSpecialRecord extends UpdatableRecordImpl<FirstSpecialRecord> implements Record18<Integer, String, Timestamp, Timestamp, Byte, BigDecimal, BigDecimal, Byte, Byte, Byte, Timestamp, Integer, Byte, String, Byte, Byte, Timestamp, Timestamp> {

    private static final long serialVersionUID = 217388167;

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.name</code>. 活动名称
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.name</code>. 活动名称
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.start_time</code>. 开始时间
     */
    public void setStartTime(Timestamp value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.start_time</code>. 开始时间
     */
    public Timestamp getStartTime() {
        return (Timestamp) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.end_time</code>. 结束日期
     */
    public void setEndTime(Timestamp value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.end_time</code>. 结束日期
     */
    public Timestamp getEndTime() {
        return (Timestamp) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.batch_discount</code>. 批量打几折
     */
    public void setBatchDiscount(Byte value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.batch_discount</code>. 批量打几折
     */
    public Byte getBatchDiscount() {
        return (Byte) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.batch_reduce</code>. 批量减多少
     */
    public void setBatchReduce(BigDecimal value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.batch_reduce</code>. 批量减多少
     */
    public BigDecimal getBatchReduce() {
        return (BigDecimal) get(5);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.batch_final_price</code>. 批量折后价
     */
    public void setBatchFinalPrice(BigDecimal value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.batch_final_price</code>. 批量折后价
     */
    public BigDecimal getBatchFinalPrice() {
        return (BigDecimal) get(6);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.is_batch_integer</code>. 是否批量取整
     */
    public void setIsBatchInteger(Byte value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.is_batch_integer</code>. 是否批量取整
     */
    public Byte getIsBatchInteger() {
        return (Byte) get(7);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.status</code>. 状态：1：启用 0：禁用
     */
    public void setStatus(Byte value) {
        set(8, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.status</code>. 状态：1：启用 0：禁用
     */
    public Byte getStatus() {
        return (Byte) get(8);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.del_flag</code>.
     */
    public void setDelFlag(Byte value) {
        set(9, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.del_flag</code>.
     */
    public Byte getDelFlag() {
        return (Byte) get(9);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.del_time</code>.
     */
    public void setDelTime(Timestamp value) {
        set(10, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.del_time</code>.
     */
    public Timestamp getDelTime() {
        return (Timestamp) get(10);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.limit_amount</code>.
     */
    public void setLimitAmount(Integer value) {
        set(11, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.limit_amount</code>.
     */
    public Integer getLimitAmount() {
        return (Integer) get(11);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.first</code>. 优先级
     */
    public void setFirst(Byte value) {
        set(12, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.first</code>. 优先级
     */
    public Byte getFirst() {
        return (Byte) get(12);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.share_config</code>. 分享设置
     */
    public void setShareConfig(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.share_config</code>. 分享设置
     */
    public String getShareConfig() {
        return (String) get(13);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.is_forever</code>. 是否永久
     */
    public void setIsForever(Byte value) {
        set(14, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.is_forever</code>. 是否永久
     */
    public Byte getIsForever() {
        return (Byte) get(14);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.limit_flag</code>. 超限购购买标记
     */
    public void setLimitFlag(Byte value) {
        set(15, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.limit_flag</code>. 超限购购买标记
     */
    public Byte getLimitFlag() {
        return (Byte) get(15);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.create_time</code>. 创建时间
     */
    public void setCreateTime(Timestamp value) {
        set(16, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.create_time</code>. 创建时间
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(16);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special.update_time</code>. 最后修改时间
     */
    public void setUpdateTime(Timestamp value) {
        set(17, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special.update_time</code>. 最后修改时间
     */
    public Timestamp getUpdateTime() {
        return (Timestamp) get(17);
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
    // Record18 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row18<Integer, String, Timestamp, Timestamp, Byte, BigDecimal, BigDecimal, Byte, Byte, Byte, Timestamp, Integer, Byte, String, Byte, Byte, Timestamp, Timestamp> fieldsRow() {
        return (Row18) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row18<Integer, String, Timestamp, Timestamp, Byte, BigDecimal, BigDecimal, Byte, Byte, Byte, Timestamp, Integer, Byte, String, Byte, Byte, Timestamp, Timestamp> valuesRow() {
        return (Row18) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return FirstSpecial.FIRST_SPECIAL.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return FirstSpecial.FIRST_SPECIAL.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field3() {
        return FirstSpecial.FIRST_SPECIAL.START_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field4() {
        return FirstSpecial.FIRST_SPECIAL.END_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field5() {
        return FirstSpecial.FIRST_SPECIAL.BATCH_DISCOUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field6() {
        return FirstSpecial.FIRST_SPECIAL.BATCH_REDUCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field7() {
        return FirstSpecial.FIRST_SPECIAL.BATCH_FINAL_PRICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field8() {
        return FirstSpecial.FIRST_SPECIAL.IS_BATCH_INTEGER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field9() {
        return FirstSpecial.FIRST_SPECIAL.STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field10() {
        return FirstSpecial.FIRST_SPECIAL.DEL_FLAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field11() {
        return FirstSpecial.FIRST_SPECIAL.DEL_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field12() {
        return FirstSpecial.FIRST_SPECIAL.LIMIT_AMOUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field13() {
        return FirstSpecial.FIRST_SPECIAL.FIRST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field14() {
        return FirstSpecial.FIRST_SPECIAL.SHARE_CONFIG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field15() {
        return FirstSpecial.FIRST_SPECIAL.IS_FOREVER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field16() {
        return FirstSpecial.FIRST_SPECIAL.LIMIT_FLAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field17() {
        return FirstSpecial.FIRST_SPECIAL.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field18() {
        return FirstSpecial.FIRST_SPECIAL.UPDATE_TIME;
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
    public Timestamp component3() {
        return getStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component4() {
        return getEndTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component5() {
        return getBatchDiscount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component6() {
        return getBatchReduce();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component7() {
        return getBatchFinalPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component8() {
        return getIsBatchInteger();
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
    public Integer component12() {
        return getLimitAmount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component13() {
        return getFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component14() {
        return getShareConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component15() {
        return getIsForever();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component16() {
        return getLimitFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component17() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component18() {
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
    public Timestamp value3() {
        return getStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value4() {
        return getEndTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value5() {
        return getBatchDiscount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value6() {
        return getBatchReduce();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value7() {
        return getBatchFinalPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value8() {
        return getIsBatchInteger();
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
    public Integer value12() {
        return getLimitAmount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value13() {
        return getFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value14() {
        return getShareConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value15() {
        return getIsForever();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value16() {
        return getLimitFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value17() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value18() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value2(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value3(Timestamp value) {
        setStartTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value4(Timestamp value) {
        setEndTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value5(Byte value) {
        setBatchDiscount(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value6(BigDecimal value) {
        setBatchReduce(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value7(BigDecimal value) {
        setBatchFinalPrice(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value8(Byte value) {
        setIsBatchInteger(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value9(Byte value) {
        setStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value10(Byte value) {
        setDelFlag(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value11(Timestamp value) {
        setDelTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value12(Integer value) {
        setLimitAmount(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value13(Byte value) {
        setFirst(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value14(String value) {
        setShareConfig(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value15(Byte value) {
        setIsForever(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value16(Byte value) {
        setLimitFlag(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value17(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord value18(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialRecord values(Integer value1, String value2, Timestamp value3, Timestamp value4, Byte value5, BigDecimal value6, BigDecimal value7, Byte value8, Byte value9, Byte value10, Timestamp value11, Integer value12, Byte value13, String value14, Byte value15, Byte value16, Timestamp value17, Timestamp value18) {
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
        value17(value17);
        value18(value18);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached FirstSpecialRecord
     */
    public FirstSpecialRecord() {
        super(FirstSpecial.FIRST_SPECIAL);
    }

    /**
     * Create a detached, initialised FirstSpecialRecord
     */
    public FirstSpecialRecord(Integer id, String name, Timestamp startTime, Timestamp endTime, Byte batchDiscount, BigDecimal batchReduce, BigDecimal batchFinalPrice, Byte isBatchInteger, Byte status, Byte delFlag, Timestamp delTime, Integer limitAmount, Byte first, String shareConfig, Byte isForever, Byte limitFlag, Timestamp createTime, Timestamp updateTime) {
        super(FirstSpecial.FIRST_SPECIAL);

        set(0, id);
        set(1, name);
        set(2, startTime);
        set(3, endTime);
        set(4, batchDiscount);
        set(5, batchReduce);
        set(6, batchFinalPrice);
        set(7, isBatchInteger);
        set(8, status);
        set(9, delFlag);
        set(10, delTime);
        set(11, limitAmount);
        set(12, first);
        set(13, shareConfig);
        set(14, isForever);
        set(15, limitFlag);
        set(16, createTime);
        set(17, updateTime);
    }
}