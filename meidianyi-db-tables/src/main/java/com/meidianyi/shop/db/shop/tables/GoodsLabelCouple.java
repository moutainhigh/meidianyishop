/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.GoodsLabelCoupleRecord;

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
public class GoodsLabelCouple extends TableImpl<GoodsLabelCoupleRecord> {

    private static final long serialVersionUID = 644023642;

    /**
     * The reference instance of <code>mini_shop_471752.b2c_goods_label_couple</code>
     */
    public static final GoodsLabelCouple GOODS_LABEL_COUPLE = new GoodsLabelCouple();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GoodsLabelCoupleRecord> getRecordType() {
        return GoodsLabelCoupleRecord.class;
    }

    /**
     * The column <code>mini_shop_471752.b2c_goods_label_couple.id</code>. 标签id
     */
    public final TableField<GoodsLabelCoupleRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "标签id");

    /**
     * The column <code>mini_shop_471752.b2c_goods_label_couple.label_id</code>. 标签id
     */
    public final TableField<GoodsLabelCoupleRecord, Integer> LABEL_ID = createField("label_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "标签id");

    /**
     * The column <code>mini_shop_471752.b2c_goods_label_couple.gta_id</code>. 商品或类型id
     */
    public final TableField<GoodsLabelCoupleRecord, Integer> GTA_ID = createField("gta_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "商品或类型id");

    /**
     * The column <code>mini_shop_471752.b2c_goods_label_couple.type</code>. 标签关联类型： 1：关联商品 2：平台分类 3店鋪分類 4：全部商品
     */
    public final TableField<GoodsLabelCoupleRecord, Byte> TYPE = createField("type", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "标签关联类型： 1：关联商品 2：平台分类 3店鋪分類 4：全部商品");

    /**
     * The column <code>mini_shop_471752.b2c_goods_label_couple.create_time</code>.
     */
    public final TableField<GoodsLabelCoupleRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_goods_label_couple.update_time</code>. 最后修改时间
     */
    public final TableField<GoodsLabelCoupleRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "最后修改时间");

    /**
     * Create a <code>mini_shop_471752.b2c_goods_label_couple</code> table reference
     */
    public GoodsLabelCouple() {
        this(DSL.name("b2c_goods_label_couple"), null);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_goods_label_couple</code> table reference
     */
    public GoodsLabelCouple(String alias) {
        this(DSL.name(alias), GOODS_LABEL_COUPLE);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_goods_label_couple</code> table reference
     */
    public GoodsLabelCouple(Name alias) {
        this(alias, GOODS_LABEL_COUPLE);
    }

    private GoodsLabelCouple(Name alias, Table<GoodsLabelCoupleRecord> aliased) {
        this(alias, aliased, null);
    }

    private GoodsLabelCouple(Name alias, Table<GoodsLabelCoupleRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> GoodsLabelCouple(Table<O> child, ForeignKey<O, GoodsLabelCoupleRecord> key) {
        super(child, key, GOODS_LABEL_COUPLE);
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
        return Arrays.<Index>asList(Indexes.GOODS_LABEL_COUPLE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<GoodsLabelCoupleRecord, Integer> getIdentity() {
        return Keys.IDENTITY_GOODS_LABEL_COUPLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<GoodsLabelCoupleRecord> getPrimaryKey() {
        return Keys.KEY_B2C_GOODS_LABEL_COUPLE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<GoodsLabelCoupleRecord>> getKeys() {
        return Arrays.<UniqueKey<GoodsLabelCoupleRecord>>asList(Keys.KEY_B2C_GOODS_LABEL_COUPLE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsLabelCouple as(String alias) {
        return new GoodsLabelCouple(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoodsLabelCouple as(Name alias) {
        return new GoodsLabelCouple(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GoodsLabelCouple rename(String name) {
        return new GoodsLabelCouple(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public GoodsLabelCouple rename(Name name) {
        return new GoodsLabelCouple(name, null);
    }
}
