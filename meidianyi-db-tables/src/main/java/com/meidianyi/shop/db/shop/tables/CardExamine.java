/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.CardExamineRecord;

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
import org.jooq.types.UInteger;


/**
 * 会员卡激活审核表
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CardExamine extends TableImpl<CardExamineRecord> {

    private static final long serialVersionUID = 661568170;

    /**
     * The reference instance of <code>jmini_shop_489258.b2c_card_examine</code>
     */
    public static final CardExamine CARD_EXAMINE = new CardExamine();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CardExamineRecord> getRecordType() {
        return CardExamineRecord.class;
    }

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.id</code>. 订单id
     */
    public final TableField<CardExamineRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "订单id");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.card_id</code>. 会云卡id
     */
    public final TableField<CardExamineRecord, Integer> CARD_ID = createField("card_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "会云卡id");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.card_no</code>. 会员卡no
     */
    public final TableField<CardExamineRecord, String> CARD_NO = createField("card_no", org.jooq.impl.SQLDataType.VARCHAR(32).nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.VARCHAR)), this, "会员卡no");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.user_id</code>. 用户id
     */
    public final TableField<CardExamineRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "用户id");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.status</code>. 审核状态 1审核中 2通过 3拒绝
     */
    public final TableField<CardExamineRecord, Byte> STATUS = createField("status", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.TINYINT)), this, "审核状态 1审核中 2通过 3拒绝");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.desc</code>. 备注
     */
    public final TableField<CardExamineRecord, String> DESC = createField("desc", org.jooq.impl.SQLDataType.VARCHAR(512), this, "备注");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.real_name</code>. 真是姓名
     */
    public final TableField<CardExamineRecord, String> REAL_NAME = createField("real_name", org.jooq.impl.SQLDataType.VARCHAR(50), this, "真是姓名");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.cid</code>. 身份证号
     */
    public final TableField<CardExamineRecord, String> CID = createField("cid", org.jooq.impl.SQLDataType.VARCHAR(18), this, "身份证号");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.province_code</code>. 所在地
     */
    public final TableField<CardExamineRecord, Integer> PROVINCE_CODE = createField("province_code", org.jooq.impl.SQLDataType.INTEGER, this, "所在地");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.city_code</code>. 所在地
     */
    public final TableField<CardExamineRecord, Integer> CITY_CODE = createField("city_code", org.jooq.impl.SQLDataType.INTEGER, this, "所在地");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.district_code</code>. 所在地
     */
    public final TableField<CardExamineRecord, Integer> DISTRICT_CODE = createField("district_code", org.jooq.impl.SQLDataType.INTEGER, this, "所在地");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.sex</code>. 性别
     */
    public final TableField<CardExamineRecord, String> SEX = createField("sex", org.jooq.impl.SQLDataType.CHAR(5), this, "性别");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.birthday_year</code>. 生日
     */
    public final TableField<CardExamineRecord, Integer> BIRTHDAY_YEAR = createField("birthday_year", org.jooq.impl.SQLDataType.INTEGER, this, "生日");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.birthday_month</code>. 生日
     */
    public final TableField<CardExamineRecord, Integer> BIRTHDAY_MONTH = createField("birthday_month", org.jooq.impl.SQLDataType.INTEGER, this, "生日");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.birthday_day</code>. 生日
     */
    public final TableField<CardExamineRecord, Integer> BIRTHDAY_DAY = createField("birthday_day", org.jooq.impl.SQLDataType.INTEGER, this, "生日");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.marital_status</code>. 婚姻状况
     */
    public final TableField<CardExamineRecord, Byte> MARITAL_STATUS = createField("marital_status", org.jooq.impl.SQLDataType.TINYINT, this, "婚姻状况");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.education</code>. 教育程度
     */
    public final TableField<CardExamineRecord, Byte> EDUCATION = createField("education", org.jooq.impl.SQLDataType.TINYINT, this, "教育程度");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.industry_info</code>. 所在行业
     */
    public final TableField<CardExamineRecord, Byte> INDUSTRY_INFO = createField("industry_info", org.jooq.impl.SQLDataType.TINYINT, this, "所在行业");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.pass_time</code>. 通过时间
     */
    public final TableField<CardExamineRecord, Timestamp> PASS_TIME = createField("pass_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0000-00-00 00:00:00", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "通过时间");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.refuse_time</code>. 拒绝时间
     */
    public final TableField<CardExamineRecord, Timestamp> REFUSE_TIME = createField("refuse_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0000-00-00 00:00:00", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "拒绝时间");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.refuse_desc</code>. 拒绝理由
     */
    public final TableField<CardExamineRecord, String> REFUSE_DESC = createField("refuse_desc", org.jooq.impl.SQLDataType.VARCHAR(512), this, "拒绝理由");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.del_flag</code>. 删除
     */
    public final TableField<CardExamineRecord, Byte> DEL_FLAG = createField("del_flag", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "删除");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.def_time</code>. 删除时间
     */
    public final TableField<CardExamineRecord, Timestamp> DEF_TIME = createField("def_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0000-00-00 00:00:00", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "删除时间");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.create_time</code>.
     */
    public final TableField<CardExamineRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.update_time</code>. 最后修改时间
     */
    public final TableField<CardExamineRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "最后修改时间");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.custom_options</code>. 自定义激活信息
     */
    public final TableField<CardExamineRecord, String> CUSTOM_OPTIONS = createField("custom_options", org.jooq.impl.SQLDataType.CLOB, this, "自定义激活信息");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.sys_id</code>. 操作员ID
     */
    public final TableField<CardExamineRecord, UInteger> SYS_ID = createField("sys_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED, this, "操作员ID");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.account_id</code>. SUB操作员ID
     */
    public final TableField<CardExamineRecord, UInteger> ACCOUNT_ID = createField("account_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED, this, "SUB操作员ID");

    /**
     * The column <code>jmini_shop_489258.b2c_card_examine.account_type</code>. 账户类型：0店铺账号，1系统账号
     */
    public final TableField<CardExamineRecord, Byte> ACCOUNT_TYPE = createField("account_type", org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "账户类型：0店铺账号，1系统账号");

    /**
     * Create a <code>jmini_shop_489258.b2c_card_examine</code> table reference
     */
    public CardExamine() {
        this(DSL.name("b2c_card_examine"), null);
    }

    /**
     * Create an aliased <code>jmini_shop_489258.b2c_card_examine</code> table reference
     */
    public CardExamine(String alias) {
        this(DSL.name(alias), CARD_EXAMINE);
    }

    /**
     * Create an aliased <code>jmini_shop_489258.b2c_card_examine</code> table reference
     */
    public CardExamine(Name alias) {
        this(alias, CARD_EXAMINE);
    }

    private CardExamine(Name alias, Table<CardExamineRecord> aliased) {
        this(alias, aliased, null);
    }

    private CardExamine(Name alias, Table<CardExamineRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("会员卡激活审核表"));
    }

    public <O extends Record> CardExamine(Table<O> child, ForeignKey<O, CardExamineRecord> key) {
        super(child, key, CARD_EXAMINE);
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
        return Arrays.<Index>asList(Indexes.CARD_EXAMINE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<CardExamineRecord, Integer> getIdentity() {
        return Keys.IDENTITY_CARD_EXAMINE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<CardExamineRecord> getPrimaryKey() {
        return Keys.KEY_B2C_CARD_EXAMINE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<CardExamineRecord>> getKeys() {
        return Arrays.<UniqueKey<CardExamineRecord>>asList(Keys.KEY_B2C_CARD_EXAMINE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardExamine as(String alias) {
        return new CardExamine(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardExamine as(Name alias) {
        return new CardExamine(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CardExamine rename(String name) {
        return new CardExamine(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CardExamine rename(Name name) {
        return new CardExamine(name, null);
    }
}