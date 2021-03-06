/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.UserCartRecordRecord;

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
public class UserCartRecord extends TableImpl<UserCartRecordRecord> {

    private static final long serialVersionUID = 1359771106;

    /**
     * The reference instance of <code>mini_shop_471752.b2c_user_cart_record</code>
     */
    public static final UserCartRecord USER_CART_RECORD = new UserCartRecord();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UserCartRecordRecord> getRecordType() {
        return UserCartRecordRecord.class;
    }

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.id</code>.
     */
    public final TableField<UserCartRecordRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.user_id</code>. 用户id
     */
    public final TableField<UserCartRecordRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "用户id");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.goods_id</code>. 商品id
     */
    public final TableField<UserCartRecordRecord, Integer> GOODS_ID = createField("goods_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "商品id");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.prd_id</code>. 规格id
     */
    public final TableField<UserCartRecordRecord, Integer> PRD_ID = createField("prd_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "规格id");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.num</code>. 件数
     */
    public final TableField<UserCartRecordRecord, Short> NUM = createField("num", org.jooq.impl.SQLDataType.SMALLINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.SMALLINT)), this, "件数");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.del_flag</code>. 0：添加，1：删除标记
     */
    public final TableField<UserCartRecordRecord, Short> DEL_FLAG = createField("del_flag", org.jooq.impl.SQLDataType.SMALLINT.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.SMALLINT)), this, "0：添加，1：删除标记");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.user_ip</code>. 用户ip
     */
    public final TableField<UserCartRecordRecord, String> USER_IP = createField("user_ip", org.jooq.impl.SQLDataType.VARCHAR(64), this, "用户ip");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.province_code</code>. 省
     */
    public final TableField<UserCartRecordRecord, String> PROVINCE_CODE = createField("province_code", org.jooq.impl.SQLDataType.VARCHAR(20), this, "省");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.province</code>. 省
     */
    public final TableField<UserCartRecordRecord, String> PROVINCE = createField("province", org.jooq.impl.SQLDataType.VARCHAR(20), this, "省");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.city_code</code>. 市
     */
    public final TableField<UserCartRecordRecord, String> CITY_CODE = createField("city_code", org.jooq.impl.SQLDataType.VARCHAR(20), this, "市");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.city</code>. 市
     */
    public final TableField<UserCartRecordRecord, String> CITY = createField("city", org.jooq.impl.SQLDataType.VARCHAR(20), this, "市");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.district_code</code>. 区
     */
    public final TableField<UserCartRecordRecord, String> DISTRICT_CODE = createField("district_code", org.jooq.impl.SQLDataType.VARCHAR(20), this, "区");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.district</code>. 区
     */
    public final TableField<UserCartRecordRecord, String> DISTRICT = createField("district", org.jooq.impl.SQLDataType.VARCHAR(20), this, "区");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.lat</code>. 经度
     */
    public final TableField<UserCartRecordRecord, String> LAT = createField("lat", org.jooq.impl.SQLDataType.VARCHAR(64), this, "经度");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.lng</code>. 纬度
     */
    public final TableField<UserCartRecordRecord, String> LNG = createField("lng", org.jooq.impl.SQLDataType.VARCHAR(64), this, "纬度");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.count</code>. 次数
     */
    public final TableField<UserCartRecordRecord, Short> COUNT = createField("count", org.jooq.impl.SQLDataType.SMALLINT.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.SMALLINT)), this, "次数");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.create_time</code>.
     */
    public final TableField<UserCartRecordRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_cart_record.update_time</code>. 最后修改时间
     */
    public final TableField<UserCartRecordRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "最后修改时间");

    /**
     * Create a <code>mini_shop_471752.b2c_user_cart_record</code> table reference
     */
    public UserCartRecord() {
        this(DSL.name("b2c_user_cart_record"), null);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_user_cart_record</code> table reference
     */
    public UserCartRecord(String alias) {
        this(DSL.name(alias), USER_CART_RECORD);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_user_cart_record</code> table reference
     */
    public UserCartRecord(Name alias) {
        this(alias, USER_CART_RECORD);
    }

    private UserCartRecord(Name alias, Table<UserCartRecordRecord> aliased) {
        this(alias, aliased, null);
    }

    private UserCartRecord(Name alias, Table<UserCartRecordRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> UserCartRecord(Table<O> child, ForeignKey<O, UserCartRecordRecord> key) {
        super(child, key, USER_CART_RECORD);
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
        return Arrays.<Index>asList(Indexes.USER_CART_RECORD_CREATE_TIME, Indexes.USER_CART_RECORD_GOODS_CREATE_TIME, Indexes.USER_CART_RECORD_PRIMARY, Indexes.USER_CART_RECORD_USER_CREATE_TIME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<UserCartRecordRecord, Integer> getIdentity() {
        return Keys.IDENTITY_USER_CART_RECORD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<UserCartRecordRecord> getPrimaryKey() {
        return Keys.KEY_B2C_USER_CART_RECORD_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<UserCartRecordRecord>> getKeys() {
        return Arrays.<UniqueKey<UserCartRecordRecord>>asList(Keys.KEY_B2C_USER_CART_RECORD_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserCartRecord as(String alias) {
        return new UserCartRecord(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserCartRecord as(Name alias) {
        return new UserCartRecord(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public UserCartRecord rename(String name) {
        return new UserCartRecord(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public UserCartRecord rename(Name name) {
        return new UserCartRecord(name, null);
    }
}
