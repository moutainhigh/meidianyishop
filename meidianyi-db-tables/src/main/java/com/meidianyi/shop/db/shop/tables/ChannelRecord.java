/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.ChannelRecordRecord;

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
public class ChannelRecord extends TableImpl<ChannelRecordRecord> {

    private static final long serialVersionUID = 223487656;

    /**
     * The reference instance of <code>mini_shop_471752.b2c_channel_record</code>
     */
    public static final ChannelRecord CHANNEL_RECORD = new ChannelRecord();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ChannelRecordRecord> getRecordType() {
        return ChannelRecordRecord.class;
    }

    /**
     * The column <code>mini_shop_471752.b2c_channel_record.id</code>. id
     */
    public final TableField<ChannelRecordRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "id");

    /**
     * The column <code>mini_shop_471752.b2c_channel_record.channel_id</code>. 渠道页id
     */
    public final TableField<ChannelRecordRecord, Integer> CHANNEL_ID = createField("channel_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "渠道页id");

    /**
     * The column <code>mini_shop_471752.b2c_channel_record.user_id</code>. userid
     */
    public final TableField<ChannelRecordRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "userid");

    /**
     * The column <code>mini_shop_471752.b2c_channel_record.type</code>. 类型 1新用 0老用户
     */
    public final TableField<ChannelRecordRecord, Byte> TYPE = createField("type", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "类型 1新用 0老用户");

    /**
     * The column <code>mini_shop_471752.b2c_channel_record.count</code>. 访问次数
     */
    public final TableField<ChannelRecordRecord, Integer> COUNT = createField("count", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "访问次数");

    /**
     * The column <code>mini_shop_471752.b2c_channel_record.create_time</code>.
     */
    public final TableField<ChannelRecordRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_channel_record.update_time</code>. 最后修改时间
     */
    public final TableField<ChannelRecordRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "最后修改时间");

    /**
     * Create a <code>mini_shop_471752.b2c_channel_record</code> table reference
     */
    public ChannelRecord() {
        this(DSL.name("b2c_channel_record"), null);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_channel_record</code> table reference
     */
    public ChannelRecord(String alias) {
        this(DSL.name(alias), CHANNEL_RECORD);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_channel_record</code> table reference
     */
    public ChannelRecord(Name alias) {
        this(alias, CHANNEL_RECORD);
    }

    private ChannelRecord(Name alias, Table<ChannelRecordRecord> aliased) {
        this(alias, aliased, null);
    }

    private ChannelRecord(Name alias, Table<ChannelRecordRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> ChannelRecord(Table<O> child, ForeignKey<O, ChannelRecordRecord> key) {
        super(child, key, CHANNEL_RECORD);
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
        return Arrays.<Index>asList(Indexes.CHANNEL_RECORD_PRIMARY, Indexes.CHANNEL_RECORD_USER_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<ChannelRecordRecord, Integer> getIdentity() {
        return Keys.IDENTITY_CHANNEL_RECORD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ChannelRecordRecord> getPrimaryKey() {
        return Keys.KEY_B2C_CHANNEL_RECORD_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ChannelRecordRecord>> getKeys() {
        return Arrays.<UniqueKey<ChannelRecordRecord>>asList(Keys.KEY_B2C_CHANNEL_RECORD_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelRecord as(String alias) {
        return new ChannelRecord(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelRecord as(Name alias) {
        return new ChannelRecord(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ChannelRecord rename(String name) {
        return new ChannelRecord(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ChannelRecord rename(Name name) {
        return new ChannelRecord(name, null);
    }
}
