/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.DistributionTag;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row13;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;
import java.math.BigDecimal;
import java.sql.Date;
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
public class DistributionTagRecord extends UpdatableRecordImpl<DistributionTagRecord> implements Record13<Integer, Date, Byte, Integer, String, Integer, BigDecimal, Integer, Integer, Integer, Integer, Timestamp, Timestamp> {

    private static final long serialVersionUID = 94978402;

    /**
     * Setter for <code>mini_shop_471752.b2c_distribution_tag.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_distribution_tag.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_distribution_tag.ref_date</code>. 日期
     */
    public void setRefDate(Date value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_distribution_tag.ref_date</code>. 日期
     */
    public Date getRefDate() {
        return (Date) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_distribution_tag.type</code>. 1,7,30
     */
    public void setType(Byte value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_distribution_tag.type</code>. 1,7,30
     */
    public Byte getType() {
        return (Byte) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_distribution_tag.tag_id</code>. 标签id
     */
    public void setTagId(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_distribution_tag.tag_id</code>. 标签id
     */
    public Integer getTagId() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_distribution_tag.tag_name</code>. 标签内容
     */
    public void setTagName(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_distribution_tag.tag_name</code>. 标签内容
     */
    public String getTagName() {
        return (String) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_distribution_tag.pay_order_num</code>. 付款订单数
     */
    public void setPayOrderNum(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_distribution_tag.pay_order_num</code>. 付款订单数
     */
    public Integer getPayOrderNum() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_distribution_tag.pay_order_money</code>. 付款金额
     */
    public void setPayOrderMoney(BigDecimal value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_distribution_tag.pay_order_money</code>. 付款金额
     */
    public BigDecimal getPayOrderMoney() {
        return (BigDecimal) get(6);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_distribution_tag.pay_user_num</code>. 付款人数
     */
    public void setPayUserNum(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_distribution_tag.pay_user_num</code>. 付款人数
     */
    public Integer getPayUserNum() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_distribution_tag.pay_goods_number</code>. 付款商品件数
     */
    public void setPayGoodsNumber(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_distribution_tag.pay_goods_number</code>. 付款商品件数
     */
    public Integer getPayGoodsNumber() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_distribution_tag.has_mobile_num</code>. 下单有手机号的用户
     */
    public void setHasMobileNum(Integer value) {
        set(9, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_distribution_tag.has_mobile_num</code>. 下单有手机号的用户
     */
    public Integer getHasMobileNum() {
        return (Integer) get(9);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_distribution_tag.has_user_num</code>. 用户数
     */
    public void setHasUserNum(Integer value) {
        set(10, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_distribution_tag.has_user_num</code>. 用户数
     */
    public Integer getHasUserNum() {
        return (Integer) get(10);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_distribution_tag.create_time</code>.
     */
    public void setCreateTime(Timestamp value) {
        set(11, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_distribution_tag.create_time</code>.
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(11);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_distribution_tag.update_time</code>. 最后修改时间
     */
    public void setUpdateTime(Timestamp value) {
        set(12, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_distribution_tag.update_time</code>. 最后修改时间
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
    public Row13<Integer, Date, Byte, Integer, String, Integer, BigDecimal, Integer, Integer, Integer, Integer, Timestamp, Timestamp> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row13<Integer, Date, Byte, Integer, String, Integer, BigDecimal, Integer, Integer, Integer, Integer, Timestamp, Timestamp> valuesRow() {
        return (Row13) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return DistributionTag.DISTRIBUTION_TAG.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field2() {
        return DistributionTag.DISTRIBUTION_TAG.REF_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field3() {
        return DistributionTag.DISTRIBUTION_TAG.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return DistributionTag.DISTRIBUTION_TAG.TAG_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return DistributionTag.DISTRIBUTION_TAG.TAG_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field6() {
        return DistributionTag.DISTRIBUTION_TAG.PAY_ORDER_NUM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field7() {
        return DistributionTag.DISTRIBUTION_TAG.PAY_ORDER_MONEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field8() {
        return DistributionTag.DISTRIBUTION_TAG.PAY_USER_NUM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field9() {
        return DistributionTag.DISTRIBUTION_TAG.PAY_GOODS_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field10() {
        return DistributionTag.DISTRIBUTION_TAG.HAS_MOBILE_NUM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field11() {
        return DistributionTag.DISTRIBUTION_TAG.HAS_USER_NUM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field12() {
        return DistributionTag.DISTRIBUTION_TAG.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field13() {
        return DistributionTag.DISTRIBUTION_TAG.UPDATE_TIME;
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
    public Date component2() {
        return getRefDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component3() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getTagId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getTagName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component6() {
        return getPayOrderNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component7() {
        return getPayOrderMoney();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component8() {
        return getPayUserNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component9() {
        return getPayGoodsNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component10() {
        return getHasMobileNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component11() {
        return getHasUserNum();
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
    public Date value2() {
        return getRefDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value3() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getTagId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getTagName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value6() {
        return getPayOrderNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value7() {
        return getPayOrderMoney();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value8() {
        return getPayUserNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value9() {
        return getPayGoodsNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value10() {
        return getHasMobileNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value11() {
        return getHasUserNum();
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
    public DistributionTagRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionTagRecord value2(Date value) {
        setRefDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionTagRecord value3(Byte value) {
        setType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionTagRecord value4(Integer value) {
        setTagId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionTagRecord value5(String value) {
        setTagName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionTagRecord value6(Integer value) {
        setPayOrderNum(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionTagRecord value7(BigDecimal value) {
        setPayOrderMoney(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionTagRecord value8(Integer value) {
        setPayUserNum(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionTagRecord value9(Integer value) {
        setPayGoodsNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionTagRecord value10(Integer value) {
        setHasMobileNum(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionTagRecord value11(Integer value) {
        setHasUserNum(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionTagRecord value12(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionTagRecord value13(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionTagRecord values(Integer value1, Date value2, Byte value3, Integer value4, String value5, Integer value6, BigDecimal value7, Integer value8, Integer value9, Integer value10, Integer value11, Timestamp value12, Timestamp value13) {
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
     * Create a detached DistributionTagRecord
     */
    public DistributionTagRecord() {
        super(DistributionTag.DISTRIBUTION_TAG);
    }

    /**
     * Create a detached, initialised DistributionTagRecord
     */
    public DistributionTagRecord(Integer id, Date refDate, Byte type, Integer tagId, String tagName, Integer payOrderNum, BigDecimal payOrderMoney, Integer payUserNum, Integer payGoodsNumber, Integer hasMobileNum, Integer hasUserNum, Timestamp createTime, Timestamp updateTime) {
        super(DistributionTag.DISTRIBUTION_TAG);

        set(0, id);
        set(1, refDate);
        set(2, type);
        set(3, tagId);
        set(4, tagName);
        set(5, payOrderNum);
        set(6, payOrderMoney);
        set(7, payUserNum);
        set(8, payGoodsNumber);
        set(9, hasMobileNum);
        set(10, hasUserNum);
        set(11, createTime);
        set(12, updateTime);
    }
}
