/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.TradesRecordRecord;

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
public class TradesRecord extends TableImpl<TradesRecordRecord> {

    private static final long serialVersionUID = -838059816;

    /**
     * The reference instance of <code>mini_shop_471752.b2c_trades_record</code>
     */
    public static final TradesRecord TRADES_RECORD = new TradesRecord();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TradesRecordRecord> getRecordType() {
        return TradesRecordRecord.class;
    }

    /**
     * The column <code>mini_shop_471752.b2c_trades_record.id</code>. 交易记录id
     */
    public final TableField<TradesRecordRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "交易记录id");

    /**
     * The column <code>mini_shop_471752.b2c_trades_record.trade_time</code>. 交易时间
     */
    public final TableField<TradesRecordRecord, Timestamp> TRADE_TIME = createField("trade_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "交易时间");

    /**
     * The column <code>mini_shop_471752.b2c_trades_record.trade_num</code>. 交易额
     */
    public final TableField<TradesRecordRecord, BigDecimal> TRADE_NUM = createField("trade_num", org.jooq.impl.SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(org.jooq.impl.DSL.inline("0.00", org.jooq.impl.SQLDataType.DECIMAL)), this, "交易额");

    /**
     * The column <code>mini_shop_471752.b2c_trades_record.user_id</code>. 交易用户id
     */
    public final TableField<TradesRecordRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "交易用户id");

    /**
     * The column <code>mini_shop_471752.b2c_trades_record.trade_content</code>. 交易内容：0：现金，1：积分
     */
    public final TableField<TradesRecordRecord, Byte> TRADE_CONTENT = createField("trade_content", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "交易内容：0：现金，1：积分");

    /**
     * The column <code>mini_shop_471752.b2c_trades_record.trade_type</code>. 交易类型说明
     */
    public final TableField<TradesRecordRecord, Byte> TRADE_TYPE = createField("trade_type", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "交易类型说明");

    /**
     * The column <code>mini_shop_471752.b2c_trades_record.trade_flow</code>. 资金流向：0：收入，1：支出，2：待确认收入
     */
    public final TableField<TradesRecordRecord, Byte> TRADE_FLOW = createField("trade_flow", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "资金流向：0：收入，1：支出，2：待确认收入");

    /**
     * The column <code>mini_shop_471752.b2c_trades_record.trade_status</code>. 交易状态：0：已入账，1：已到账
     */
    public final TableField<TradesRecordRecord, Byte> TRADE_STATUS = createField("trade_status", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "交易状态：0：已入账，1：已到账");

    /**
     * The column <code>mini_shop_471752.b2c_trades_record.trade_sn</code>. 交易单号
     */
    public final TableField<TradesRecordRecord, String> TRADE_SN = createField("trade_sn", org.jooq.impl.SQLDataType.VARCHAR(20).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "交易单号");

    /**
     * The column <code>mini_shop_471752.b2c_trades_record.create_time</code>.
     */
    public final TableField<TradesRecordRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_trades_record.update_time</code>. 最后修改时间
     */
    public final TableField<TradesRecordRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "最后修改时间");

    /**
     * Create a <code>mini_shop_471752.b2c_trades_record</code> table reference
     */
    public TradesRecord() {
        this(DSL.name("b2c_trades_record"), null);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_trades_record</code> table reference
     */
    public TradesRecord(String alias) {
        this(DSL.name(alias), TRADES_RECORD);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_trades_record</code> table reference
     */
    public TradesRecord(Name alias) {
        this(alias, TRADES_RECORD);
    }

    private TradesRecord(Name alias, Table<TradesRecordRecord> aliased) {
        this(alias, aliased, null);
    }

    private TradesRecord(Name alias, Table<TradesRecordRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> TradesRecord(Table<O> child, ForeignKey<O, TradesRecordRecord> key) {
        super(child, key, TRADES_RECORD);
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
        return Arrays.<Index>asList(Indexes.TRADES_RECORD_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<TradesRecordRecord, Integer> getIdentity() {
        return Keys.IDENTITY_TRADES_RECORD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<TradesRecordRecord> getPrimaryKey() {
        return Keys.KEY_B2C_TRADES_RECORD_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<TradesRecordRecord>> getKeys() {
        return Arrays.<UniqueKey<TradesRecordRecord>>asList(Keys.KEY_B2C_TRADES_RECORD_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TradesRecord as(String alias) {
        return new TradesRecord(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TradesRecord as(Name alias) {
        return new TradesRecord(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TradesRecord rename(String name) {
        return new TradesRecord(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TradesRecord rename(Name name) {
        return new TradesRecord(name, null);
    }
}