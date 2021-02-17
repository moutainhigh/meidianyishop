/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.TradesRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import javax.annotation.Generated;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;


/**
 * 交易统计 每小时统计数据
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Trades extends TableImpl<TradesRecord> {

    private static final long serialVersionUID = 602728167;

    /**
     * The reference instance of <code>jmini_shop_666666.b2c_trades</code>
     */
    public static final Trades TRADES = new Trades();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TradesRecord> getRecordType() {
        return TradesRecord.class;
    }

    /**
     * The column <code>jmini_shop_666666.b2c_trades.ref_date</code>. 日期：例2018-09-05
     */
    public final TableField<TradesRecord, Date> REF_DATE = createField("ref_date", org.jooq.impl.SQLDataType.DATE, this, "日期：例2018-09-05");

    /**
     * The column <code>jmini_shop_666666.b2c_trades.hour</code>. 小时：10时
     */
    public final TableField<TradesRecord, Byte> HOUR = createField("hour", org.jooq.impl.SQLDataType.TINYINT, this, "小时：10时");

    /**
     * The column <code>jmini_shop_666666.b2c_trades.uv</code>. 访客数
     */
    public final TableField<TradesRecord, Integer> UV = createField("uv", org.jooq.impl.SQLDataType.INTEGER, this, "访客数");

    /**
     * The column <code>jmini_shop_666666.b2c_trades.pv</code>. 访问量
     */
    public final TableField<TradesRecord, Integer> PV = createField("pv", org.jooq.impl.SQLDataType.INTEGER, this, "访问量");

    /**
     * The column <code>jmini_shop_666666.b2c_trades.pay_user_num</code>. 付款人数（包括货到付款发货后的状态）
     */
    public final TableField<TradesRecord, Integer> PAY_USER_NUM = createField("pay_user_num", org.jooq.impl.SQLDataType.INTEGER, this, "付款人数（包括货到付款发货后的状态）");

    /**
     * The column <code>jmini_shop_666666.b2c_trades.pay_order_num</code>. 付款订单数
     */
    public final TableField<TradesRecord, Integer> PAY_ORDER_NUM = createField("pay_order_num", org.jooq.impl.SQLDataType.INTEGER, this, "付款订单数");

    /**
     * The column <code>jmini_shop_666666.b2c_trades.pay_order_money</code>. 付款金额
     */
    public final TableField<TradesRecord, BigDecimal> PAY_ORDER_MONEY = createField("pay_order_money", org.jooq.impl.SQLDataType.DECIMAL(10, 2), this, "付款金额");

    /**
     * The column <code>jmini_shop_666666.b2c_trades.pay_goods_number</code>. 付款件数
     */
    public final TableField<TradesRecord, Integer> PAY_GOODS_NUMBER = createField("pay_goods_number", org.jooq.impl.SQLDataType.INTEGER, this, "付款件数");

    /**
     * The column <code>jmini_shop_666666.b2c_trades.pct</code>. 客单价
     */
    public final TableField<TradesRecord, BigDecimal> PCT = createField("pct", org.jooq.impl.SQLDataType.DECIMAL(10, 2), this, "客单价");

    /**
     * The column <code>jmini_shop_666666.b2c_trades.uv_pay_ratio</code>. 转化率
     */
    public final TableField<TradesRecord, BigDecimal> UV_PAY_RATIO = createField("uv_pay_ratio", org.jooq.impl.SQLDataType.DECIMAL(4, 2), this, "转化率");

    /**
     * The column <code>jmini_shop_666666.b2c_trades.create_time</code>.
     */
    public final TableField<TradesRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>jmini_shop_666666.b2c_trades.update_time</code>. 最后修改时间
     */
    public final TableField<TradesRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "最后修改时间");

    /**
     * The column <code>jmini_shop_666666.b2c_trades.id</code>.
     */
    public final TableField<TradesRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * Create a <code>jmini_shop_666666.b2c_trades</code> table reference
     */
    public Trades() {
        this(DSL.name("b2c_trades"), null);
    }

    /**
     * Create an aliased <code>jmini_shop_666666.b2c_trades</code> table reference
     */
    public Trades(String alias) {
        this(DSL.name(alias), TRADES);
    }

    /**
     * Create an aliased <code>jmini_shop_666666.b2c_trades</code> table reference
     */
    public Trades(Name alias) {
        this(alias, TRADES);
    }

    private Trades(Name alias, Table<TradesRecord> aliased) {
        this(alias, aliased, null);
    }

    private Trades(Name alias, Table<TradesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("交易统计 每小时统计数据"));
    }

    public <O extends Record> Trades(Table<O> child, ForeignKey<O, TradesRecord> key) {
        super(child, key, TRADES);
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
        return Arrays.<Index>asList(Indexes.TRADES_PRIMARY, Indexes.TRADES_REF_DATE, Indexes.TRADES_REF_HOUR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<TradesRecord, Integer> getIdentity() {
        return Keys.IDENTITY_TRADES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<TradesRecord> getPrimaryKey() {
        return Keys.KEY_B2C_TRADES_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<TradesRecord>> getKeys() {
        return Arrays.<UniqueKey<TradesRecord>>asList(Keys.KEY_B2C_TRADES_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Trades as(String alias) {
        return new Trades(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Trades as(Name alias) {
        return new Trades(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Trades rename(String name) {
        return new Trades(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Trades rename(Name name) {
        return new Trades(name, null);
    }
}