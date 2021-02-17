/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.GoodsFromHisRecord;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * his商品-药品信息表
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GoodsFromHis extends TableImpl<GoodsFromHisRecord> {

    private static final long serialVersionUID = 470189602;

    /**
     * The reference instance of <code>mini_shop_471752.b2c_goods_from_his</code>
     */
    public static final GoodsFromHis GOODS_FROM_HIS = new GoodsFromHis();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GoodsFromHisRecord> getRecordType() {
        return GoodsFromHisRecord.class;
    }

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.id</code>. 商品额外信息id
     */
    public final TableField<GoodsFromHisRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "商品额外信息id");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.goods_price</code>.
     */
    public final TableField<GoodsFromHisRecord, BigDecimal> GOODS_PRICE = createField("goods_price", org.jooq.impl.SQLDataType.DECIMAL(10, 2), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.goods_number</code>.
     */
    public final TableField<GoodsFromHisRecord, Integer> GOODS_NUMBER = createField("goods_number", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.goods_code</code>. 药品唯一编码
     */
    public final TableField<GoodsFromHisRecord, String> GOODS_CODE = createField("goods_code", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "药品唯一编码");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.goods_common_name</code>. 通用名
     */
    public final TableField<GoodsFromHisRecord, String> GOODS_COMMON_NAME = createField("goods_common_name", org.jooq.impl.SQLDataType.VARCHAR(512).nullable(false).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "通用名");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.is_medical</code>.
     */
    public final TableField<GoodsFromHisRecord, Byte> IS_MEDICAL = createField("is_medical", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("1", org.jooq.impl.SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.goods_alias_name</code>. 别名
     */
    public final TableField<GoodsFromHisRecord, String> GOODS_ALIAS_NAME = createField("goods_alias_name", org.jooq.impl.SQLDataType.VARCHAR(512).nullable(false).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "别名");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.goods_quality_ratio</code>. 规格系数，通用名和规格系数确定一个药品
     */
    public final TableField<GoodsFromHisRecord, String> GOODS_QUALITY_RATIO = createField("goods_quality_ratio", org.jooq.impl.SQLDataType.VARCHAR(512).nullable(false).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "规格系数，通用名和规格系数确定一个药品");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.is_rx</code>. 是否处方药 0否 1是 默认0
     */
    public final TableField<GoodsFromHisRecord, Byte> IS_RX = createField("is_rx", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "是否处方药 0否 1是 默认0");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.medical_type</code>. null 未知  1西药 2中成药 3中草药
     */
    public final TableField<GoodsFromHisRecord, Byte> MEDICAL_TYPE = createField("medical_type", org.jooq.impl.SQLDataType.TINYINT, this, "null 未知  1西药 2中成药 3中草药");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.insurance_flag</code>. 医保类型 1:甲 2:乙 3:丙 4:科研
     */
    public final TableField<GoodsFromHisRecord, Byte> INSURANCE_FLAG = createField("insurance_flag", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "医保类型 1:甲 2:乙 3:丙 4:科研");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.insurance_code</code>. 医保编码
     */
    public final TableField<GoodsFromHisRecord, String> INSURANCE_CODE = createField("insurance_code", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "医保编码");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.insurance_database_name</code>. 医保库内名称
     */
    public final TableField<GoodsFromHisRecord, String> INSURANCE_DATABASE_NAME = createField("insurance_database_name", org.jooq.impl.SQLDataType.VARCHAR(128).nullable(false).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "医保库内名称");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.goods_basic_unit</code>. 商品基本单位
     */
    public final TableField<GoodsFromHisRecord, String> GOODS_BASIC_UNIT = createField("goods_basic_unit", org.jooq.impl.SQLDataType.VARCHAR(32).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "商品基本单位");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.goods_package_unit</code>. 商品包装单位
     */
    public final TableField<GoodsFromHisRecord, String> GOODS_PACKAGE_UNIT = createField("goods_package_unit", org.jooq.impl.SQLDataType.VARCHAR(32).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "商品包装单位");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.goods_unit_convert_factor</code>. 整包转换系数
     */
    public final TableField<GoodsFromHisRecord, Double> GOODS_UNIT_CONVERT_FACTOR = createField("goods_unit_convert_factor", org.jooq.impl.SQLDataType.DOUBLE.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.DOUBLE)), this, "整包转换系数");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.goods_equivalent_quantity</code>. 等效量
     */
    public final TableField<GoodsFromHisRecord, Double> GOODS_EQUIVALENT_QUANTITY = createField("goods_equivalent_quantity", org.jooq.impl.SQLDataType.DOUBLE.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.DOUBLE)), this, "等效量");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.goods_equivalent_unit</code>. 等效单位
     */
    public final TableField<GoodsFromHisRecord, String> GOODS_EQUIVALENT_UNIT = createField("goods_equivalent_unit", org.jooq.impl.SQLDataType.VARCHAR(32).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "等效单位");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.goods_approval_number</code>. 批准文号
     */
    public final TableField<GoodsFromHisRecord, String> GOODS_APPROVAL_NUMBER = createField("goods_approval_number", org.jooq.impl.SQLDataType.VARCHAR(128).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "批准文号");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.goods_production_enterprise</code>. 生产企业
     */
    public final TableField<GoodsFromHisRecord, String> GOODS_PRODUCTION_ENTERPRISE = createField("goods_production_enterprise", org.jooq.impl.SQLDataType.VARCHAR(512).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "生产企业");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.is_delete</code>.
     */
    public final TableField<GoodsFromHisRecord, Byte> IS_DELETE = createField("is_delete", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.state</code>.
     */
    public final TableField<GoodsFromHisRecord, Integer> STATE = createField("state", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.is_match</code>.
     */
    public final TableField<GoodsFromHisRecord, Byte> IS_MATCH = createField("is_match", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.create_time</code>.
     */
    public final TableField<GoodsFromHisRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_goods_from_his.update_time</code>. 最后修改时间
     */
    public final TableField<GoodsFromHisRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "最后修改时间");

    /**
     * Create a <code>mini_shop_471752.b2c_goods_from_his</code> table reference
     */
    public GoodsFromHis() {
        this(DSL.name("b2c_goods_from_his"), null);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_goods_from_his</code> table reference
     */
    public GoodsFromHis(String alias) {
        this(DSL.name(alias), GOODS_FROM_HIS);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_goods_from_his</code> table reference
     */
    public GoodsFromHis(Name alias) {
        this(alias, GOODS_FROM_HIS);
    }

    private GoodsFromHis(Name alias, Table<GoodsFromHisRecord> aliased) {
        this(alias, aliased, null);
    }

    private GoodsFromHis(Name alias, Table<GoodsFromHisRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("his商品-药品信息表"));
    }

    public <O extends Record> GoodsFromHis(Table<O> child, ForeignKey<O, GoodsFromHisRecord> key) {
        super(child, key, GOODS_FROM_HIS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return MiniShop_471752.MINI_SHOP_471752;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.GOODS_FROM_HIS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<GoodsFromHisRecord, Integer> getIdentity() {
        return Keys.IDENTITY_GOODS_FROM_HIS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<GoodsFromHisRecord> getPrimaryKey() {
        return Keys.KEY_B2C_GOODS_FROM_HIS_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<GoodsFromHisRecord>> getKeys() {
        return Arrays.<UniqueKey<GoodsFromHisRecord>>asList(Keys.KEY_B2C_GOODS_FROM_HIS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsFromHis as(String alias) {
        return new GoodsFromHis(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsFromHis as(Name alias) {
        return new GoodsFromHis(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GoodsFromHis rename(String name) {
        return new GoodsFromHis(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public GoodsFromHis rename(Name name) {
        return new GoodsFromHis(name, null);
    }
}