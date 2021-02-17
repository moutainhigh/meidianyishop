/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.main.tables;


import com.meidianyi.shop.db.main.Indexes;
import com.meidianyi.shop.db.main.Keys;
import com.meidianyi.shop.db.main.MiniMain;
import com.meidianyi.shop.db.main.tables.records.ActivityStatisticsRecord;

import java.math.BigDecimal;
import java.sql.Date;
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
public class ActivityStatistics extends TableImpl<ActivityStatisticsRecord> {

    private static final long serialVersionUID = -2001440740;

    /**
     * The reference instance of <code>mini_main.b2c_activity_statistics</code>
     */
    public static final ActivityStatistics ACTIVITY_STATISTICS = new ActivityStatistics();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ActivityStatisticsRecord> getRecordType() {
        return ActivityStatisticsRecord.class;
    }

    /**
     * The column <code>mini_main.b2c_activity_statistics.id</code>.
     */
    public final TableField<ActivityStatisticsRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>mini_main.b2c_activity_statistics.ref_date</code>. 2018-09-18
     */
    public final TableField<ActivityStatisticsRecord, Date> REF_DATE = createField("ref_date", org.jooq.impl.SQLDataType.DATE.nullable(false), this, "2018-09-18");

    /**
     * The column <code>mini_main.b2c_activity_statistics.type</code>. 1，7，30
     */
    public final TableField<ActivityStatisticsRecord, Byte> TYPE = createField("type", org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "1，7，30");

    /**
     * The column <code>mini_main.b2c_activity_statistics.activity_type</code>. 活动类型
     */
    public final TableField<ActivityStatisticsRecord, Byte> ACTIVITY_TYPE = createField("activity_type", org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "活动类型");

    /**
     * The column <code>mini_main.b2c_activity_statistics.shop_id</code>. 店铺ID
     */
    public final TableField<ActivityStatisticsRecord, Integer> SHOP_ID = createField("shop_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "店铺ID");

    /**
     * The column <code>mini_main.b2c_activity_statistics.shop_version</code>. 店铺版本
     */
    public final TableField<ActivityStatisticsRecord, String> SHOP_VERSION = createField("shop_version", org.jooq.impl.SQLDataType.VARCHAR(11).nullable(false), this, "店铺版本");

    /**
     * The column <code>mini_main.b2c_activity_statistics.currently</code>. 进行中的活动
     */
    public final TableField<ActivityStatisticsRecord, Integer> CURRENTLY = createField("currently", org.jooq.impl.SQLDataType.INTEGER, this, "进行中的活动");

    /**
     * The column <code>mini_main.b2c_activity_statistics.expired</code>. 过期中的活动
     */
    public final TableField<ActivityStatisticsRecord, Integer> EXPIRED = createField("expired", org.jooq.impl.SQLDataType.INTEGER, this, "过期中的活动");

    /**
     * The column <code>mini_main.b2c_activity_statistics.closed</code>. 关闭中的活动（截止到统计时间）
     */
    public final TableField<ActivityStatisticsRecord, Integer> CLOSED = createField("closed", org.jooq.impl.SQLDataType.INTEGER, this, "关闭中的活动（截止到统计时间）");

    /**
     * The column <code>mini_main.b2c_activity_statistics.order_num</code>. 活动订单
     */
    public final TableField<ActivityStatisticsRecord, Integer> ORDER_NUM = createField("order_num", org.jooq.impl.SQLDataType.INTEGER, this, "活动订单");

    /**
     * The column <code>mini_main.b2c_activity_statistics.order_money</code>. 活动订单金额
     */
    public final TableField<ActivityStatisticsRecord, BigDecimal> ORDER_MONEY = createField("order_money", org.jooq.impl.SQLDataType.DECIMAL(10, 2), this, "活动订单金额");

    /**
     * The column <code>mini_main.b2c_activity_statistics.visited</code>. 活动访问用户数
     */
    public final TableField<ActivityStatisticsRecord, Integer> VISITED = createField("visited", org.jooq.impl.SQLDataType.INTEGER, this, "活动访问用户数");

    /**
     * The column <code>mini_main.b2c_activity_statistics.visited_user</code>. 访问用户数
     */
    public final TableField<ActivityStatisticsRecord, Integer> VISITED_USER = createField("visited_user", org.jooq.impl.SQLDataType.INTEGER, this, "访问用户数");

    /**
     * The column <code>mini_main.b2c_activity_statistics.join_user</code>. 参与用户数
     */
    public final TableField<ActivityStatisticsRecord, Integer> JOIN_USER = createField("join_user", org.jooq.impl.SQLDataType.INTEGER, this, "参与用户数");

    /**
     * The column <code>mini_main.b2c_activity_statistics.success_user</code>. 成功用户数
     */
    public final TableField<ActivityStatisticsRecord, Integer> SUCCESS_USER = createField("success_user", org.jooq.impl.SQLDataType.INTEGER, this, "成功用户数");

    /**
     * The column <code>mini_main.b2c_activity_statistics.share_user</code>. 分享用户数
     */
    public final TableField<ActivityStatisticsRecord, Integer> SHARE_USER = createField("share_user", org.jooq.impl.SQLDataType.INTEGER, this, "分享用户数");

    /**
     * The column <code>mini_main.b2c_activity_statistics.used_user</code>. 使用用户数
     */
    public final TableField<ActivityStatisticsRecord, Integer> USED_USER = createField("used_user", org.jooq.impl.SQLDataType.INTEGER, this, "使用用户数");

    /**
     * The column <code>mini_main.b2c_activity_statistics.bargain_user_count</code>. 砍价人次
     */
    public final TableField<ActivityStatisticsRecord, Integer> BARGAIN_USER_COUNT = createField("bargain_user_count", org.jooq.impl.SQLDataType.INTEGER, this, "砍价人次");

    /**
     * The column <code>mini_main.b2c_activity_statistics.new_user</code>. 拉新数
     */
    public final TableField<ActivityStatisticsRecord, Integer> NEW_USER = createField("new_user", org.jooq.impl.SQLDataType.INTEGER, this, "拉新数");

    /**
     * Create a <code>mini_main.b2c_activity_statistics</code> table reference
     */
    public ActivityStatistics() {
        this(DSL.name("b2c_activity_statistics"), null);
    }

    /**
     * Create an aliased <code>mini_main.b2c_activity_statistics</code> table reference
     */
    public ActivityStatistics(String alias) {
        this(DSL.name(alias), ACTIVITY_STATISTICS);
    }

    /**
     * Create an aliased <code>mini_main.b2c_activity_statistics</code> table reference
     */
    public ActivityStatistics(Name alias) {
        this(alias, ACTIVITY_STATISTICS);
    }

    private ActivityStatistics(Name alias, Table<ActivityStatisticsRecord> aliased) {
        this(alias, aliased, null);
    }

    private ActivityStatistics(Name alias, Table<ActivityStatisticsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> ActivityStatistics(Table<O> child, ForeignKey<O, ActivityStatisticsRecord> key) {
        super(child, key, ACTIVITY_STATISTICS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return MiniMain.MINI_MAIN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.ACTIVITY_STATISTICS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<ActivityStatisticsRecord, Integer> getIdentity() {
        return Keys.IDENTITY_ACTIVITY_STATISTICS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ActivityStatisticsRecord> getPrimaryKey() {
        return Keys.KEY_B2C_ACTIVITY_STATISTICS_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ActivityStatisticsRecord>> getKeys() {
        return Arrays.<UniqueKey<ActivityStatisticsRecord>>asList(Keys.KEY_B2C_ACTIVITY_STATISTICS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityStatistics as(String alias) {
        return new ActivityStatistics(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityStatistics as(Name alias) {
        return new ActivityStatistics(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ActivityStatistics rename(String name) {
        return new ActivityStatistics(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ActivityStatistics rename(Name name) {
        return new ActivityStatistics(name, null);
    }
}