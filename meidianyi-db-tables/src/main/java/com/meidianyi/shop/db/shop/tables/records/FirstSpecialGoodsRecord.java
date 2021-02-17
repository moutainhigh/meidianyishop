/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.FirstSpecialGoods;

import java.math.BigDecimal;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
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
public class FirstSpecialGoodsRecord extends UpdatableRecordImpl<FirstSpecialGoodsRecord> implements Record6<Integer, Integer, Integer, BigDecimal, BigDecimal, BigDecimal> {

    private static final long serialVersionUID = 1065231135;

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special_goods.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special_goods.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special_goods.first_special_id</code>. 限时减价活动ID
     */
    public void setFirstSpecialId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special_goods.first_special_id</code>. 限时减价活动ID
     */
    public Integer getFirstSpecialId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special_goods.goods_id</code>. 商品ID
     */
    public void setGoodsId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special_goods.goods_id</code>. 商品ID
     */
    public Integer getGoodsId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special_goods.discount</code>. 打几折
     */
    public void setDiscount(BigDecimal value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special_goods.discount</code>. 打几折
     */
    public BigDecimal getDiscount() {
        return (BigDecimal) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special_goods.reduce_price</code>. 减多少钱
     */
    public void setReducePrice(BigDecimal value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special_goods.reduce_price</code>. 减多少钱
     */
    public BigDecimal getReducePrice() {
        return (BigDecimal) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_first_special_goods.goods_price</code>. 折后价格
     */
    public void setGoodsPrice(BigDecimal value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_first_special_goods.goods_price</code>. 折后价格
     */
    public BigDecimal getGoodsPrice() {
        return (BigDecimal) get(5);
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
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, Integer, Integer, BigDecimal, BigDecimal, BigDecimal> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, Integer, Integer, BigDecimal, BigDecimal, BigDecimal> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return FirstSpecialGoods.FIRST_SPECIAL_GOODS.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return FirstSpecialGoods.FIRST_SPECIAL_GOODS.FIRST_SPECIAL_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return FirstSpecialGoods.FIRST_SPECIAL_GOODS.GOODS_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field4() {
        return FirstSpecialGoods.FIRST_SPECIAL_GOODS.DISCOUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field5() {
        return FirstSpecialGoods.FIRST_SPECIAL_GOODS.REDUCE_PRICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field6() {
        return FirstSpecialGoods.FIRST_SPECIAL_GOODS.GOODS_PRICE;
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
        return getFirstSpecialId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component3() {
        return getGoodsId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component4() {
        return getDiscount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component5() {
        return getReducePrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component6() {
        return getGoodsPrice();
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
        return getFirstSpecialId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getGoodsId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value4() {
        return getDiscount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value5() {
        return getReducePrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value6() {
        return getGoodsPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialGoodsRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialGoodsRecord value2(Integer value) {
        setFirstSpecialId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialGoodsRecord value3(Integer value) {
        setGoodsId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialGoodsRecord value4(BigDecimal value) {
        setDiscount(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialGoodsRecord value5(BigDecimal value) {
        setReducePrice(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialGoodsRecord value6(BigDecimal value) {
        setGoodsPrice(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstSpecialGoodsRecord values(Integer value1, Integer value2, Integer value3, BigDecimal value4, BigDecimal value5, BigDecimal value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached FirstSpecialGoodsRecord
     */
    public FirstSpecialGoodsRecord() {
        super(FirstSpecialGoods.FIRST_SPECIAL_GOODS);
    }

    /**
     * Create a detached, initialised FirstSpecialGoodsRecord
     */
    public FirstSpecialGoodsRecord(Integer id, Integer firstSpecialId, Integer goodsId, BigDecimal discount, BigDecimal reducePrice, BigDecimal goodsPrice) {
        super(FirstSpecialGoods.FIRST_SPECIAL_GOODS);

        set(0, id);
        set(1, firstSpecialId);
        set(2, goodsId);
        set(3, discount);
        set(4, reducePrice);
        set(5, goodsPrice);
    }
}