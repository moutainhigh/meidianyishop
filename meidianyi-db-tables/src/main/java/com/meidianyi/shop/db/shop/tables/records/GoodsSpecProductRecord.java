/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.GoodsSpecProduct;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record19;
import org.jooq.Row19;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;
import java.math.BigDecimal;
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
public class GoodsSpecProductRecord extends UpdatableRecordImpl<GoodsSpecProductRecord> implements Record19<Integer, Integer, Integer, BigDecimal, BigDecimal, BigDecimal, Integer, String, String, String, String, Byte, Byte, String, String, Byte, Timestamp, Timestamp, BigDecimal> {

    private static final long serialVersionUID = -1272079940;

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.prd_id</code>.
     */
    public void setPrdId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.prd_id</code>.
     */
    public Integer getPrdId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.shop_id</code>.
     */
    public void setShopId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.shop_id</code>.
     */
    public Integer getShopId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.goods_id</code>.
     */
    public void setGoodsId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.goods_id</code>.
     */
    public Integer getGoodsId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.prd_price</code>.
     */
    public void setPrdPrice(BigDecimal value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.prd_price</code>.
     */
    public BigDecimal getPrdPrice() {
        return (BigDecimal) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.prd_market_price</code>. 市场价格
     */
    public void setPrdMarketPrice(BigDecimal value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.prd_market_price</code>. 市场价格
     */
    public BigDecimal getPrdMarketPrice() {
        return (BigDecimal) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.prd_cost_price</code>. 成本价
     */
    public void setPrdCostPrice(BigDecimal value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.prd_cost_price</code>. 成本价
     */
    public BigDecimal getPrdCostPrice() {
        return (BigDecimal) get(5);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.prd_number</code>. 当前规格组合产品库存
     */
    public void setPrdNumber(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.prd_number</code>. 当前规格组合产品库存
     */
    public Integer getPrdNumber() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.prd_sn</code>. 商家编码
     */
    public void setPrdSn(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.prd_sn</code>. 商家编码
     */
    public String getPrdSn() {
        return (String) get(7);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.prd_codes</code>. 商品条码
     */
    public void setPrdCodes(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.prd_codes</code>. 商品条码
     */
    public String getPrdCodes() {
        return (String) get(8);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.prd_specs</code>.
     */
    public void setPrdSpecs(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.prd_specs</code>.
     */
    public String getPrdSpecs() {
        return (String) get(9);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.prd_desc</code>. 规格描述，格式例子：颜色:红色 尺码:s
     */
    public void setPrdDesc(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.prd_desc</code>. 规格描述，格式例子：颜色:红色 尺码:s
     */
    public String getPrdDesc() {
        return (String) get(10);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.del_flag</code>.
     */
    public void setDelFlag(Byte value) {
        set(11, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.del_flag</code>.
     */
    public Byte getDelFlag() {
        return (Byte) get(11);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.self_flag</code>. 1:商家自己添加商品，其他没用
     */
    public void setSelfFlag(Byte value) {
        set(12, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.self_flag</code>. 1:商家自己添加商品，其他没用
     */
    public Byte getSelfFlag() {
        return (Byte) get(12);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.low_shop_price</code>. 最低售出价格
     */
    public void setLowShopPrice(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.low_shop_price</code>. 最低售出价格
     */
    public String getLowShopPrice() {
        return (String) get(13);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.prd_img</code>. 图片地址
     */
    public void setPrdImg(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.prd_img</code>. 图片地址
     */
    public String getPrdImg() {
        return (String) get(14);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.price_flag</code>. 0:商家未改价，1：商家改价，2：批量改价，3：毛利改价
     */
    public void setPriceFlag(Byte value) {
        set(15, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.price_flag</code>. 0:商家未改价，1：商家改价，2：批量改价，3：毛利改价
     */
    public Byte getPriceFlag() {
        return (Byte) get(15);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.create_time</code>.
     */
    public void setCreateTime(Timestamp value) {
        set(16, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.create_time</code>.
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(16);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.update_time</code>. 最后修改时间
     */
    public void setUpdateTime(Timestamp value) {
        set(17, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.update_time</code>. 最后修改时间
     */
    public Timestamp getUpdateTime() {
        return (Timestamp) get(17);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_goods_spec_product.prd_weight</code>. 规格重量
     */
    public void setPrdWeight(BigDecimal value) {
        set(18, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_goods_spec_product.prd_weight</code>. 规格重量
     */
    public BigDecimal getPrdWeight() {
        return (BigDecimal) get(18);
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
    // Record19 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row19<Integer, Integer, Integer, BigDecimal, BigDecimal, BigDecimal, Integer, String, String, String, String, Byte, Byte, String, String, Byte, Timestamp, Timestamp, BigDecimal> fieldsRow() {
        return (Row19) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row19<Integer, Integer, Integer, BigDecimal, BigDecimal, BigDecimal, Integer, String, String, String, String, Byte, Byte, String, String, Byte, Timestamp, Timestamp, BigDecimal> valuesRow() {
        return (Row19) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.PRD_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.SHOP_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.GOODS_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field4() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.PRD_PRICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field5() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.PRD_MARKET_PRICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field6() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.PRD_COST_PRICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field7() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.PRD_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.PRD_SN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.PRD_CODES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.PRD_SPECS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.PRD_DESC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field12() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.DEL_FLAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field13() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.SELF_FLAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field14() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.LOW_SHOP_PRICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field15() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.PRD_IMG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field16() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.PRICE_FLAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field17() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field18() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.UPDATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field19() {
        return GoodsSpecProduct.GOODS_SPEC_PRODUCT.PRD_WEIGHT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getPrdId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component2() {
        return getShopId();
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
        return getPrdPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component5() {
        return getPrdMarketPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component6() {
        return getPrdCostPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component7() {
        return getPrdNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getPrdSn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component9() {
        return getPrdCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component10() {
        return getPrdSpecs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component11() {
        return getPrdDesc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component12() {
        return getDelFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component13() {
        return getSelfFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component14() {
        return getLowShopPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component15() {
        return getPrdImg();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component16() {
        return getPriceFlag();
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
    public BigDecimal component19() {
        return getPrdWeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getPrdId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getShopId();
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
        return getPrdPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value5() {
        return getPrdMarketPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value6() {
        return getPrdCostPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value7() {
        return getPrdNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getPrdSn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getPrdCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getPrdSpecs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getPrdDesc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value12() {
        return getDelFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value13() {
        return getSelfFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value14() {
        return getLowShopPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value15() {
        return getPrdImg();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value16() {
        return getPriceFlag();
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
    public BigDecimal value19() {
        return getPrdWeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value1(Integer value) {
        setPrdId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value2(Integer value) {
        setShopId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value3(Integer value) {
        setGoodsId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value4(BigDecimal value) {
        setPrdPrice(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value5(BigDecimal value) {
        setPrdMarketPrice(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value6(BigDecimal value) {
        setPrdCostPrice(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value7(Integer value) {
        setPrdNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value8(String value) {
        setPrdSn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value9(String value) {
        setPrdCodes(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value10(String value) {
        setPrdSpecs(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value11(String value) {
        setPrdDesc(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value12(Byte value) {
        setDelFlag(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value13(Byte value) {
        setSelfFlag(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value14(String value) {
        setLowShopPrice(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value15(String value) {
        setPrdImg(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value16(Byte value) {
        setPriceFlag(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value17(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value18(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord value19(BigDecimal value) {
        setPrdWeight(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsSpecProductRecord values(Integer value1, Integer value2, Integer value3, BigDecimal value4, BigDecimal value5, BigDecimal value6, Integer value7, String value8, String value9, String value10, String value11, Byte value12, Byte value13, String value14, String value15, Byte value16, Timestamp value17, Timestamp value18, BigDecimal value19) {
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
        value19(value19);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached GoodsSpecProductRecord
     */
    public GoodsSpecProductRecord() {
        super(GoodsSpecProduct.GOODS_SPEC_PRODUCT);
    }

    /**
     * Create a detached, initialised GoodsSpecProductRecord
     */
    public GoodsSpecProductRecord(Integer prdId, Integer shopId, Integer goodsId, BigDecimal prdPrice, BigDecimal prdMarketPrice, BigDecimal prdCostPrice, Integer prdNumber, String prdSn, String prdCodes, String prdSpecs, String prdDesc, Byte delFlag, Byte selfFlag, String lowShopPrice, String prdImg, Byte priceFlag, Timestamp createTime, Timestamp updateTime, BigDecimal prdWeight) {
        super(GoodsSpecProduct.GOODS_SPEC_PRODUCT);

        set(0, prdId);
        set(1, shopId);
        set(2, goodsId);
        set(3, prdPrice);
        set(4, prdMarketPrice);
        set(5, prdCostPrice);
        set(6, prdNumber);
        set(7, prdSn);
        set(8, prdCodes);
        set(9, prdSpecs);
        set(10, prdDesc);
        set(11, delFlag);
        set(12, selfFlag);
        set(13, lowShopPrice);
        set(14, prdImg);
        set(15, priceFlag);
        set(16, createTime);
        set(17, updateTime);
        set(18, prdWeight);
    }
}
