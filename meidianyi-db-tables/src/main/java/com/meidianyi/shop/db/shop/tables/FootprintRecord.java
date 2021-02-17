/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.FootprintRecordRecord;

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
public class FootprintRecord extends TableImpl<FootprintRecordRecord> {

    private static final long serialVersionUID = 757424215;

    /**
     * The reference instance of <code>mini_shop_471752.b2c_footprint_record</code>
     */
    public static final FootprintRecord FOOTPRINT_RECORD = new FootprintRecord();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FootprintRecordRecord> getRecordType() {
        return FootprintRecordRecord.class;
    }

    /**
     * The column <code>mini_shop_471752.b2c_footprint_record.id</code>.
     */
    public final TableField<FootprintRecordRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_footprint_record.goods_id</code>. 商品id
     */
    public final TableField<FootprintRecordRecord, Integer> GOODS_ID = createField("goods_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "商品id");

    /**
     * The column <code>mini_shop_471752.b2c_footprint_record.user_id</code>.
     */
    public final TableField<FootprintRecordRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_footprint_record.count</code>. 浏览次数
     */
    public final TableField<FootprintRecordRecord, Integer> COUNT = createField("count", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.INTEGER)), this, "浏览次数");

    /**
     * The column <code>mini_shop_471752.b2c_footprint_record.type</code>. 0 老用户 1新用户
     */
    public final TableField<FootprintRecordRecord, Byte> TYPE = createField("type", org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "0 老用户 1新用户");

    /**
     * The column <code>mini_shop_471752.b2c_footprint_record.create_time</code>.
     */
    public final TableField<FootprintRecordRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_footprint_record.update_time</code>. 最后修改时间
     */
    public final TableField<FootprintRecordRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "最后修改时间");

    /**
     * Create a <code>mini_shop_471752.b2c_footprint_record</code> table reference
     */
    public FootprintRecord() {
        this(DSL.name("b2c_footprint_record"), null);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_footprint_record</code> table reference
     */
    public FootprintRecord(String alias) {
        this(DSL.name(alias), FOOTPRINT_RECORD);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_footprint_record</code> table reference
     */
    public FootprintRecord(Name alias) {
        this(alias, FOOTPRINT_RECORD);
    }

    private FootprintRecord(Name alias, Table<FootprintRecordRecord> aliased) {
        this(alias, aliased, null);
    }

    private FootprintRecord(Name alias, Table<FootprintRecordRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> FootprintRecord(Table<O> child, ForeignKey<O, FootprintRecordRecord> key) {
        super(child, key, FOOTPRINT_RECORD);
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
        return Arrays.<Index>asList(Indexes.FOOTPRINT_RECORD_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<FootprintRecordRecord, Integer> getIdentity() {
        return Keys.IDENTITY_FOOTPRINT_RECORD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<FootprintRecordRecord> getPrimaryKey() {
        return Keys.KEY_B2C_FOOTPRINT_RECORD_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<FootprintRecordRecord>> getKeys() {
        return Arrays.<UniqueKey<FootprintRecordRecord>>asList(Keys.KEY_B2C_FOOTPRINT_RECORD_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FootprintRecord as(String alias) {
        return new FootprintRecord(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FootprintRecord as(Name alias) {
        return new FootprintRecord(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public FootprintRecord rename(String name) {
        return new FootprintRecord(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public FootprintRecord rename(Name name) {
        return new FootprintRecord(name, null);
    }
}