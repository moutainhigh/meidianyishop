/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.GoodsSummary;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record18;
import org.jooq.Row18;
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
public class GoodsSummaryRecord extends UpdatableRecordImpl<GoodsSummaryRecord> implements Record18<Integer, Date, Byte, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, BigDecimal, Integer, Integer, Integer, Integer, Timestamp, Timestamp> {

    private static final long serialVersionUID = -1069618844;

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.ref_date</code>. 统计日期
     */
    public void setRefDate(Date value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.ref_date</code>. 统计日期
     */
    public Date getRefDate() {
        return (Date) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.type</code>. 1,7,30
     */
    public void setType(Byte value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.type</code>. 1,7,30
     */
    public Byte getType() {
        return (Byte) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.goods_id</code>.
     */
    public void setGoodsId(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.goods_id</code>.
     */
    public Integer getGoodsId() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.new_user_number</code>. 新成交客户数
     */
    public void setNewUserNumber(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.new_user_number</code>. 新成交客户数
     */
    public Integer getNewUserNumber() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.old_user_number</code>. 老成交客户数
     */
    public void setOldUserNumber(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.old_user_number</code>. 老成交客户数
     */
    public Integer getOldUserNumber() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.pv</code>. 浏览量
     */
    public void setPv(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.pv</code>. 浏览量
     */
    public Integer getPv() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.uv</code>. 访客数
     */
    public void setUv(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.uv</code>. 访客数
     */
    public Integer getUv() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.cart_uv</code>. 加购人数
     */
    public void setCartUv(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.cart_uv</code>. 加购人数
     */
    public Integer getCartUv() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.paid_uv</code>. 付款人数
     */
    public void setPaidUv(Integer value) {
        set(9, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.paid_uv</code>. 付款人数
     */
    public Integer getPaidUv() {
        return (Integer) get(9);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.paid_goods_number</code>. 付款商品件数
     */
    public void setPaidGoodsNumber(Integer value) {
        set(10, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.paid_goods_number</code>. 付款商品件数
     */
    public Integer getPaidGoodsNumber() {
        return (Integer) get(10);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.goods_sales</code>. 销售额
     */
    public void setGoodsSales(BigDecimal value) {
        set(11, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.goods_sales</code>. 销售额
     */
    public BigDecimal getGoodsSales() {
        return (BigDecimal) get(11);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.recommend_user_num</code>. 推荐人数
     */
    public void setRecommendUserNum(Integer value) {
        set(12, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.recommend_user_num</code>. 推荐人数
     */
    public Integer getRecommendUserNum() {
        return (Integer) get(12);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.collect_use_num</code>. 收藏人数
     */
    public void setCollectUseNum(Integer value) {
        set(13, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.collect_use_num</code>. 收藏人数
     */
    public Integer getCollectUseNum() {
        return (Integer) get(13);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.share_pv</code>. 分享次数
     */
    public void setSharePv(Integer value) {
        set(14, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.share_pv</code>. 分享次数
     */
    public Integer getSharePv() {
        return (Integer) get(14);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.share_uv</code>. 分享人数
     */
    public void setShareUv(Integer value) {
        set(15, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.share_uv</code>. 分享人数
     */
    public Integer getShareUv() {
        return (Integer) get(15);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.create_time</code>.
     */
    public void setCreateTime(Timestamp value) {
        set(16, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.create_time</code>.
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(16);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_summary.update_time</code>. 最后修改时间
     */
    public void setUpdateTime(Timestamp value) {
        set(17, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_summary.update_time</code>. 最后修改时间
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
    public Row18<Integer, Date, Byte, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, BigDecimal, Integer, Integer, Integer, Integer, Timestamp, Timestamp> fieldsRow() {
        return (Row18) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row18<Integer, Date, Byte, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, BigDecimal, Integer, Integer, Integer, Integer, Timestamp, Timestamp> valuesRow() {
        return (Row18) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return GoodsSummary.GOODS_SUMMARY.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field2() {
        return GoodsSummary.GOODS_SUMMARY.REF_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field3() {
        return GoodsSummary.GOODS_SUMMARY.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return GoodsSummary.GOODS_SUMMARY.GOODS_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field5() {
        return GoodsSummary.GOODS_SUMMARY.NEW_USER_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field6() {
        return GoodsSummary.GOODS_SUMMARY.OLD_USER_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field7() {
        return GoodsSummary.GOODS_SUMMARY.PV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field8() {
        return GoodsSummary.GOODS_SUMMARY.UV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field9() {
        return GoodsSummary.GOODS_SUMMARY.CART_UV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field10() {
        return GoodsSummary.GOODS_SUMMARY.PAID_UV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field11() {
        return GoodsSummary.GOODS_SUMMARY.PAID_GOODS_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field12() {
        return GoodsSummary.GOODS_SUMMARY.GOODS_SALES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field13() {
        return GoodsSummary.GOODS_SUMMARY.RECOMMEND_USER_NUM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field14() {
        return GoodsSummary.GOODS_SUMMARY.COLLECT_USE_NUM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field15() {
        return GoodsSummary.GOODS_SUMMARY.SHARE_PV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field16() {
        return GoodsSummary.GOODS_SUMMARY.SHARE_UV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field17() {
        return GoodsSummary.GOODS_SUMMARY.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field18() {
        return GoodsSummary.GOODS_SUMMARY.UPDATE_TIME;
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
        return getGoodsId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component5() {
        return getNewUserNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component6() {
        return getOldUserNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component7() {
        return getPv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component8() {
        return getUv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component9() {
        return getCartUv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component10() {
        return getPaidUv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component11() {
        return getPaidGoodsNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component12() {
        return getGoodsSales();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component13() {
        return getRecommendUserNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component14() {
        return getCollectUseNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component15() {
        return getSharePv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component16() {
        return getShareUv();
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
        return getGoodsId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value5() {
        return getNewUserNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value6() {
        return getOldUserNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value7() {
        return getPv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value8() {
        return getUv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value9() {
        return getCartUv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value10() {
        return getPaidUv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value11() {
        return getPaidGoodsNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value12() {
        return getGoodsSales();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value13() {
        return getRecommendUserNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value14() {
        return getCollectUseNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value15() {
        return getSharePv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value16() {
        return getShareUv();
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
    public GoodsSummaryRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value2(Date value) {
        setRefDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value3(Byte value) {
        setType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value4(Integer value) {
        setGoodsId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value5(Integer value) {
        setNewUserNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value6(Integer value) {
        setOldUserNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value7(Integer value) {
        setPv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value8(Integer value) {
        setUv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value9(Integer value) {
        setCartUv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value10(Integer value) {
        setPaidUv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value11(Integer value) {
        setPaidGoodsNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value12(BigDecimal value) {
        setGoodsSales(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value13(Integer value) {
        setRecommendUserNum(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value14(Integer value) {
        setCollectUseNum(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value15(Integer value) {
        setSharePv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value16(Integer value) {
        setShareUv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value17(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord value18(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSummaryRecord values(Integer value1, Date value2, Byte value3, Integer value4, Integer value5, Integer value6, Integer value7, Integer value8, Integer value9, Integer value10, Integer value11, BigDecimal value12, Integer value13, Integer value14, Integer value15, Integer value16, Timestamp value17, Timestamp value18) {
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
     * Create a detached GoodsSummaryRecord
     */
    public GoodsSummaryRecord() {
        super(GoodsSummary.GOODS_SUMMARY);
    }

    /**
     * Create a detached, initialised GoodsSummaryRecord
     */
    public GoodsSummaryRecord(Integer id, Date refDate, Byte type, Integer goodsId, Integer newUserNumber, Integer oldUserNumber, Integer pv, Integer uv, Integer cartUv, Integer paidUv, Integer paidGoodsNumber, BigDecimal goodsSales, Integer recommendUserNum, Integer collectUseNum, Integer sharePv, Integer shareUv, Timestamp createTime, Timestamp updateTime) {
        super(GoodsSummary.GOODS_SUMMARY);

        set(0, id);
        set(1, refDate);
        set(2, type);
        set(3, goodsId);
        set(4, newUserNumber);
        set(5, oldUserNumber);
        set(6, pv);
        set(7, uv);
        set(8, cartUv);
        set(9, paidUv);
        set(10, paidGoodsNumber);
        set(11, goodsSales);
        set(12, recommendUserNum);
        set(13, collectUseNum);
        set(14, sharePv);
        set(15, shareUv);
        set(16, createTime);
        set(17, updateTime);
    }
}
