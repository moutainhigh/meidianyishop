/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.IntegralMallProduct;

import java.math.BigDecimal;
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
public class IntegralMallProductRecord extends UpdatableRecordImpl<IntegralMallProductRecord> implements Record8<Integer, Integer, Integer, Integer, Short, BigDecimal, Timestamp, Timestamp> {

    private static final long serialVersionUID = 329992936;

    /**
     * Setter for <code>mini_shop_471752.b2c_integral_mall_product.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_integral_mall_product.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_integral_mall_product.integral_mall_define_id</code>. 积分商城活动定义表id
     */
    public void setIntegralMallDefineId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_integral_mall_product.integral_mall_define_id</code>. 积分商城活动定义表id
     */
    public Integer getIntegralMallDefineId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_integral_mall_product.product_id</code>. 规格产品id
     */
    public void setProductId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_integral_mall_product.product_id</code>. 规格产品id
     */
    public Integer getProductId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_integral_mall_product.score</code>. 积分数
     */
    public void setScore(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_integral_mall_product.score</code>. 积分数
     */
    public Integer getScore() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_integral_mall_product.stock</code>. 库存数
     */
    public void setStock(Short value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_integral_mall_product.stock</code>. 库存数
     */
    public Short getStock() {
        return (Short) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_integral_mall_product.money</code>. 兑换现金
     */
    public void setMoney(BigDecimal value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_integral_mall_product.money</code>. 兑换现金
     */
    public BigDecimal getMoney() {
        return (BigDecimal) get(5);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_integral_mall_product.create_time</code>.
     */
    public void setCreateTime(Timestamp value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_integral_mall_product.create_time</code>.
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(6);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_integral_mall_product.update_time</code>. 最后修改时间
     */
    public void setUpdateTime(Timestamp value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_integral_mall_product.update_time</code>. 最后修改时间
     */
    public Timestamp getUpdateTime() {
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
    public Row8<Integer, Integer, Integer, Integer, Short, BigDecimal, Timestamp, Timestamp> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<Integer, Integer, Integer, Integer, Short, BigDecimal, Timestamp, Timestamp> valuesRow() {
        return (Row8) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return IntegralMallProduct.INTEGRAL_MALL_PRODUCT.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return IntegralMallProduct.INTEGRAL_MALL_PRODUCT.INTEGRAL_MALL_DEFINE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return IntegralMallProduct.INTEGRAL_MALL_PRODUCT.PRODUCT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return IntegralMallProduct.INTEGRAL_MALL_PRODUCT.SCORE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field5() {
        return IntegralMallProduct.INTEGRAL_MALL_PRODUCT.STOCK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field6() {
        return IntegralMallProduct.INTEGRAL_MALL_PRODUCT.MONEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field7() {
        return IntegralMallProduct.INTEGRAL_MALL_PRODUCT.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field8() {
        return IntegralMallProduct.INTEGRAL_MALL_PRODUCT.UPDATE_TIME;
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
        return getIntegralMallDefineId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component3() {
        return getProductId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getScore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short component5() {
        return getStock();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component6() {
        return getMoney();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component7() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component8() {
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
    public Integer value2() {
        return getIntegralMallDefineId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getProductId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getScore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value5() {
        return getStock();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value6() {
        return getMoney();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value7() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value8() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegralMallProductRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegralMallProductRecord value2(Integer value) {
        setIntegralMallDefineId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegralMallProductRecord value3(Integer value) {
        setProductId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegralMallProductRecord value4(Integer value) {
        setScore(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegralMallProductRecord value5(Short value) {
        setStock(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegralMallProductRecord value6(BigDecimal value) {
        setMoney(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegralMallProductRecord value7(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegralMallProductRecord value8(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegralMallProductRecord values(Integer value1, Integer value2, Integer value3, Integer value4, Short value5, BigDecimal value6, Timestamp value7, Timestamp value8) {
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
     * Create a detached IntegralMallProductRecord
     */
    public IntegralMallProductRecord() {
        super(IntegralMallProduct.INTEGRAL_MALL_PRODUCT);
    }

    /**
     * Create a detached, initialised IntegralMallProductRecord
     */
    public IntegralMallProductRecord(Integer id, Integer integralMallDefineId, Integer productId, Integer score, Short stock, BigDecimal money, Timestamp createTime, Timestamp updateTime) {
        super(IntegralMallProduct.INTEGRAL_MALL_PRODUCT);

        set(0, id);
        set(1, integralMallDefineId);
        set(2, productId);
        set(3, score);
        set(4, stock);
        set(5, money);
        set(6, createTime);
        set(7, updateTime);
    }
}