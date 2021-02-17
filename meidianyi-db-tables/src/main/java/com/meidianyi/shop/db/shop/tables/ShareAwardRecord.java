/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.ShareAwardRecordRecord;

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
 * 用户分享记录表
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ShareAwardRecord extends TableImpl<ShareAwardRecordRecord> {

    private static final long serialVersionUID = 1369213096;

    /**
     * The reference instance of <code>mini_shop_471752.b2c_share_award_record</code>
     */
    public static final ShareAwardRecord SHARE_AWARD_RECORD = new ShareAwardRecord();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ShareAwardRecordRecord> getRecordType() {
        return ShareAwardRecordRecord.class;
    }

    /**
     * The column <code>mini_shop_471752.b2c_share_award_record.id</code>.
     */
    public final TableField<ShareAwardRecordRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_share_award_record.user_id</code>. 用户ID
     */
    public final TableField<ShareAwardRecordRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "用户ID");

    /**
     * The column <code>mini_shop_471752.b2c_share_award_record.share_id</code>. 活动ID
     */
    public final TableField<ShareAwardRecordRecord, Integer> SHARE_ID = createField("share_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "活动ID");

    /**
     * The column <code>mini_shop_471752.b2c_share_award_record.goods_id</code>. 商品ID
     */
    public final TableField<ShareAwardRecordRecord, Integer> GOODS_ID = createField("goods_id", org.jooq.impl.SQLDataType.INTEGER.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "商品ID");

    /**
     * The column <code>mini_shop_471752.b2c_share_award_record.user_number</code>. 分享链接被查看用户数
     */
    public final TableField<ShareAwardRecordRecord, Integer> USER_NUMBER = createField("user_number", org.jooq.impl.SQLDataType.INTEGER.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "分享链接被查看用户数");

    /**
     * The column <code>mini_shop_471752.b2c_share_award_record.status</code>. 活动现在处于等级 1，2,3分别对应b2c_share_award表中的first_level_rule，second_level_rule，third_level_rule
     */
    public final TableField<ShareAwardRecordRecord, Byte> STATUS = createField("status", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "活动现在处于等级 1，2,3分别对应b2c_share_award表中的first_level_rule，second_level_rule，third_level_rule");

    /**
     * The column <code>mini_shop_471752.b2c_share_award_record.first_award</code>. 1级规则奖品状态 0进行中 1未领取 2已领取 3已过期
     */
    public final TableField<ShareAwardRecordRecord, Byte> FIRST_AWARD = createField("first_award", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "1级规则奖品状态 0进行中 1未领取 2已领取 3已过期");

    /**
     * The column <code>mini_shop_471752.b2c_share_award_record.second_award</code>. 2级规则奖品状态 0进行中 1未领取 2已领取 3已过期
     */
    public final TableField<ShareAwardRecordRecord, Byte> SECOND_AWARD = createField("second_award", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "2级规则奖品状态 0进行中 1未领取 2已领取 3已过期");

    /**
     * The column <code>mini_shop_471752.b2c_share_award_record.third_award</code>. 3级规则奖品状态 0进行中 1未领取 2已领取 3已过期
     */
    public final TableField<ShareAwardRecordRecord, Byte> THIRD_AWARD = createField("third_award", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "3级规则奖品状态 0进行中 1未领取 2已领取 3已过期");

    /**
     * The column <code>mini_shop_471752.b2c_share_award_record.create_time</code>. 创建时间
     */
    public final TableField<ShareAwardRecordRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "创建时间");

    /**
     * The column <code>mini_shop_471752.b2c_share_award_record.update_time</code>. 更新时间
     */
    public final TableField<ShareAwardRecordRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "更新时间");

    /**
     * Create a <code>mini_shop_471752.b2c_share_award_record</code> table reference
     */
    public ShareAwardRecord() {
        this(DSL.name("b2c_share_award_record"), null);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_share_award_record</code> table reference
     */
    public ShareAwardRecord(String alias) {
        this(DSL.name(alias), SHARE_AWARD_RECORD);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_share_award_record</code> table reference
     */
    public ShareAwardRecord(Name alias) {
        this(alias, SHARE_AWARD_RECORD);
    }

    private ShareAwardRecord(Name alias, Table<ShareAwardRecordRecord> aliased) {
        this(alias, aliased, null);
    }

    private ShareAwardRecord(Name alias, Table<ShareAwardRecordRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("用户分享记录表"));
    }

    public <O extends Record> ShareAwardRecord(Table<O> child, ForeignKey<O, ShareAwardRecordRecord> key) {
        super(child, key, SHARE_AWARD_RECORD);
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
        return Arrays.<Index>asList(Indexes.SHARE_AWARD_RECORD_PRIMARY, Indexes.SHARE_AWARD_RECORD_SHARE_ID, Indexes.SHARE_AWARD_RECORD_USER_SHARE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<ShareAwardRecordRecord, Integer> getIdentity() {
        return Keys.IDENTITY_SHARE_AWARD_RECORD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ShareAwardRecordRecord> getPrimaryKey() {
        return Keys.KEY_B2C_SHARE_AWARD_RECORD_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ShareAwardRecordRecord>> getKeys() {
        return Arrays.<UniqueKey<ShareAwardRecordRecord>>asList(Keys.KEY_B2C_SHARE_AWARD_RECORD_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShareAwardRecord as(String alias) {
        return new ShareAwardRecord(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShareAwardRecord as(Name alias) {
        return new ShareAwardRecord(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ShareAwardRecord rename(String name) {
        return new ShareAwardRecord(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ShareAwardRecord rename(Name name) {
        return new ShareAwardRecord(name, null);
    }
}