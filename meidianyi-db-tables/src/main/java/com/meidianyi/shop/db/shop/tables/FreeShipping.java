/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.FreeShippingRecord;
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

import javax.annotation.Generated;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;


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
public class FreeShipping extends TableImpl<FreeShippingRecord> {

    private static final long serialVersionUID = -1904509332;

    /**
     * The reference instance of <code>mini_shop_4748160.b2c_free_shipping</code>
     */
    public static final FreeShipping FREE_SHIPPING = new FreeShipping();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FreeShippingRecord> getRecordType() {
        return FreeShippingRecord.class;
    }

    /**
     * The column <code>mini_shop_4748160.b2c_free_shipping.id</code>.
     */
    public final TableField<FreeShippingRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>mini_shop_4748160.b2c_free_shipping.name</code>. 活动名称
     */
    public final TableField<FreeShippingRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "活动名称");

    /**
     * The column <code>mini_shop_4748160.b2c_free_shipping.expire_type</code>. 0:固定日期 1：永久有效
     */
    public final TableField<FreeShippingRecord, Byte> EXPIRE_TYPE = createField("expire_type", org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "0:固定日期 1：永久有效");

    /**
     * The column <code>mini_shop_4748160.b2c_free_shipping.start_time</code>. 开始时间
     */
    public final TableField<FreeShippingRecord, Timestamp> START_TIME = createField("start_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "开始时间");

    /**
     * The column <code>mini_shop_4748160.b2c_free_shipping.end_time</code>. 结束时间
     */
    public final TableField<FreeShippingRecord, Timestamp> END_TIME = createField("end_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "结束时间");

    /**
     * The column <code>mini_shop_4748160.b2c_free_shipping.type</code>. 条件 1全部 0部分
     */
    public final TableField<FreeShippingRecord, Byte> TYPE = createField("type", org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "条件 1全部 0部分");

    /**
     * The column <code>mini_shop_4748160.b2c_free_shipping.recommend_goods_id</code>. 指定商品可用
     */
    public final TableField<FreeShippingRecord, String> RECOMMEND_GOODS_ID = createField("recommend_goods_id", org.jooq.impl.SQLDataType.CLOB, this, "指定商品可用");

    /**
     * The column <code>mini_shop_4748160.b2c_free_shipping.recommend_cat_id</code>. 指定分类可用
     */
    public final TableField<FreeShippingRecord, String> RECOMMEND_CAT_ID = createField("recommend_cat_id", org.jooq.impl.SQLDataType.CLOB, this, "指定分类可用");

    /**
     * The column <code>mini_shop_4748160.b2c_free_shipping.recommend_sort_id</code>. 指定商家分类可用
     */
    public final TableField<FreeShippingRecord, String> RECOMMEND_SORT_ID = createField("recommend_sort_id", org.jooq.impl.SQLDataType.CLOB, this, "指定商家分类可用");

    /**
     * The column <code>mini_shop_4748160.b2c_free_shipping.status</code>. 1停用
     */
    public final TableField<FreeShippingRecord, Byte> STATUS = createField("status", org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "1停用");

    /**
     * The column <code>mini_shop_4748160.b2c_free_shipping.create_time</code>.
     */
    public final TableField<FreeShippingRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>mini_shop_4748160.b2c_free_shipping.update_time</code>. 最后修改时间
     */
    public final TableField<FreeShippingRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "最后修改时间");

    /**
     * The column <code>mini_shop_4748160.b2c_free_shipping.del_flag</code>. 1删除
     */
    public final TableField<FreeShippingRecord, Byte> DEL_FLAG = createField("del_flag", org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "1删除");

    /**
     * The column <code>mini_shop_4748160.b2c_free_shipping.del_time</code>.
     */
    public final TableField<FreeShippingRecord, Timestamp> DEL_TIME = createField("del_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>mini_shop_4748160.b2c_free_shipping.level</code>. 优先级 默认0
     */
    public final TableField<FreeShippingRecord, Byte> LEVEL = createField("level", org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "优先级 默认0");

    /**
     * Create a <code>mini_shop_4748160.b2c_free_shipping</code> table reference
     */
    public FreeShipping() {
        this(DSL.name("b2c_free_shipping"), null);
    }

    /**
     * Create an aliased <code>mini_shop_4748160.b2c_free_shipping</code> table reference
     */
    public FreeShipping(String alias) {
        this(DSL.name(alias), FREE_SHIPPING);
    }

    /**
     * Create an aliased <code>mini_shop_4748160.b2c_free_shipping</code> table reference
     */
    public FreeShipping(Name alias) {
        this(alias, FREE_SHIPPING);
    }

    private FreeShipping(Name alias, Table<FreeShippingRecord> aliased) {
        this(alias, aliased, null);
    }

    private FreeShipping(Name alias, Table<FreeShippingRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> FreeShipping(Table<O> child, ForeignKey<O, FreeShippingRecord> key) {
        super(child, key, FREE_SHIPPING);
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
        return Arrays.<Index>asList(Indexes.FREE_SHIPPING_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<FreeShippingRecord, Integer> getIdentity() {
        return Keys.IDENTITY_FREE_SHIPPING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<FreeShippingRecord> getPrimaryKey() {
        return Keys.KEY_B2C_FREE_SHIPPING_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<FreeShippingRecord>> getKeys() {
        return Arrays.<UniqueKey<FreeShippingRecord>>asList(Keys.KEY_B2C_FREE_SHIPPING_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FreeShipping as(String alias) {
        return new FreeShipping(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FreeShipping as(Name alias) {
        return new FreeShipping(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public FreeShipping rename(String name) {
        return new FreeShipping(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public FreeShipping rename(Name name) {
        return new FreeShipping(name, null);
    }
}