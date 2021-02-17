/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.UserAddressRecord;

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
public class UserAddress extends TableImpl<UserAddressRecord> {

    private static final long serialVersionUID = -1335887088;

    /**
     * The reference instance of <code>mini_shop_471752.b2c_user_address</code>
     */
    public static final UserAddress USER_ADDRESS = new UserAddress();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UserAddressRecord> getRecordType() {
        return UserAddressRecord.class;
    }

    /**
     * The column <code>mini_shop_471752.b2c_user_address.address_id</code>.
     */
    public final TableField<UserAddressRecord, Integer> ADDRESS_ID = createField("address_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.address_name</code>.
     */
    public final TableField<UserAddressRecord, String> ADDRESS_NAME = createField("address_name", org.jooq.impl.SQLDataType.VARCHAR(50).nullable(false).defaultValue(DSL.inline("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.user_id</code>.
     */
    public final TableField<UserAddressRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.user_cid</code>.
     */
    public final TableField<UserAddressRecord, String> USER_CID = createField("user_cid", org.jooq.impl.SQLDataType.VARCHAR(40).nullable(false).defaultValue(DSL.inline("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.wx_openid</code>.
     */
    public final TableField<UserAddressRecord, String> WX_OPENID = createField("wx_openid", org.jooq.impl.SQLDataType.VARCHAR(128).nullable(false).defaultValue(DSL.inline("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.consignee</code>.
     */
    public final TableField<UserAddressRecord, String> CONSIGNEE = createField("consignee", org.jooq.impl.SQLDataType.VARCHAR(60).nullable(false).defaultValue(DSL.inline("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.email</code>.
     */
    public final TableField<UserAddressRecord, String> EMAIL = createField("email", org.jooq.impl.SQLDataType.VARCHAR(60).nullable(false).defaultValue(DSL.inline("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.country_code</code>.
     */
    public final TableField<UserAddressRecord, Integer> COUNTRY_CODE = createField("country_code", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.province_name</code>.
     */
    public final TableField<UserAddressRecord, String> PROVINCE_NAME = createField("province_name", org.jooq.impl.SQLDataType.VARCHAR(191).nullable(false).defaultValue(DSL.inline("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.province_code</code>.
     */
    public final TableField<UserAddressRecord, Integer> PROVINCE_CODE = createField("province_code", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.city_code</code>.
     */
    public final TableField<UserAddressRecord, Integer> CITY_CODE = createField("city_code", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.city_name</code>.
     */
    public final TableField<UserAddressRecord, String> CITY_NAME = createField("city_name", org.jooq.impl.SQLDataType.VARCHAR(191).nullable(false).defaultValue(DSL.inline("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.district_code</code>.
     */
    public final TableField<UserAddressRecord, Integer> DISTRICT_CODE = createField("district_code", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.district_name</code>.
     */
    public final TableField<UserAddressRecord, String> DISTRICT_NAME = createField("district_name", org.jooq.impl.SQLDataType.VARCHAR(191).nullable(false).defaultValue(DSL.inline("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.address</code>.
     */
    public final TableField<UserAddressRecord, String> ADDRESS = createField("address", org.jooq.impl.SQLDataType.VARCHAR(120).nullable(false).defaultValue(DSL.inline("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.complete_address</code>.
     */
    public final TableField<UserAddressRecord, String> COMPLETE_ADDRESS = createField("complete_address", org.jooq.impl.SQLDataType.VARCHAR(191).nullable(false).defaultValue(DSL.inline("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.zipcode</code>.
     */
    public final TableField<UserAddressRecord, String> ZIPCODE = createField("zipcode", org.jooq.impl.SQLDataType.VARCHAR(60).nullable(false).defaultValue(DSL.inline("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.tel</code>.
     */
    public final TableField<UserAddressRecord, String> TEL = createField("tel", org.jooq.impl.SQLDataType.VARCHAR(60).nullable(false).defaultValue(DSL.inline("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.mobile</code>.
     */
    public final TableField<UserAddressRecord, String> MOBILE = createField("mobile", org.jooq.impl.SQLDataType.VARCHAR(60).nullable(false).defaultValue(DSL.inline("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.best_time</code>.
     */
    public final TableField<UserAddressRecord, String> BEST_TIME = createField("best_time", org.jooq.impl.SQLDataType.VARCHAR(120).nullable(false).defaultValue(DSL.inline("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.is_default</code>.
     */
    public final TableField<UserAddressRecord, Byte> IS_DEFAULT = createField("is_default", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.last_used_time</code>.
     */
    public final TableField<UserAddressRecord, Timestamp> LAST_USED_TIME = createField("last_used_time", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(DSL.inline("NULL", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.create_time</code>.
     */
    public final TableField<UserAddressRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("current_timestamp()", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.update_time</code>. 最后修改时间
     */
    public final TableField<UserAddressRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("current_timestamp()", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "最后修改时间");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.lat</code>. 纬度
     */
    public final TableField<UserAddressRecord, String> LAT = createField("lat", org.jooq.impl.SQLDataType.VARCHAR(20).defaultValue(DSL.inline("NULL", org.jooq.impl.SQLDataType.VARCHAR)), this, "纬度");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.lng</code>. 经度
     */
    public final TableField<UserAddressRecord, String> LNG = createField("lng", org.jooq.impl.SQLDataType.VARCHAR(20).defaultValue(DSL.inline("NULL", org.jooq.impl.SQLDataType.VARCHAR)), this, "经度");

    /**
     * The column <code>mini_shop_471752.b2c_user_address.del_flag</code>. 0默认 1删除
     */
    public final TableField<UserAddressRecord, Byte> DEL_FLAG = createField("del_flag", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "0默认 1删除");

    /**
     * Create a <code>mini_shop_471752.b2c_user_address</code> table reference
     */
    public UserAddress() {
        this(DSL.name("b2c_user_address"), null);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_user_address</code> table reference
     */
    public UserAddress(String alias) {
        this(DSL.name(alias), USER_ADDRESS);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_user_address</code> table reference
     */
    public UserAddress(Name alias) {
        this(alias, USER_ADDRESS);
    }

    private UserAddress(Name alias, Table<UserAddressRecord> aliased) {
        this(alias, aliased, null);
    }

    private UserAddress(Name alias, Table<UserAddressRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> UserAddress(Table<O> child, ForeignKey<O, UserAddressRecord> key) {
        super(child, key, USER_ADDRESS);
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
        return Arrays.<Index>asList(Indexes.USER_ADDRESS_PRIMARY, Indexes.USER_ADDRESS_USER_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<UserAddressRecord, Integer> getIdentity() {
        return Keys.IDENTITY_USER_ADDRESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<UserAddressRecord> getPrimaryKey() {
        return Keys.KEY_B2C_USER_ADDRESS_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<UserAddressRecord>> getKeys() {
        return Arrays.<UniqueKey<UserAddressRecord>>asList(Keys.KEY_B2C_USER_ADDRESS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAddress as(String alias) {
        return new UserAddress(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAddress as(Name alias) {
        return new UserAddress(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public UserAddress rename(String name) {
        return new UserAddress(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public UserAddress rename(Name name) {
        return new UserAddress(name, null);
    }
}