/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import com.meidianyi.shop.db.shop.tables.records.DistributorApplyRecord;
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
 * 分销原申请记录
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DistributorApply extends TableImpl<DistributorApplyRecord> {

    private static final long serialVersionUID = 400601530;

    /**
     * The reference instance of <code>mini_shop_471752.b2c_distributor_apply</code>
     */
    public static final DistributorApply DISTRIBUTOR_APPLY = new DistributorApply();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DistributorApplyRecord> getRecordType() {
        return DistributorApplyRecord.class;
    }

    /**
     * The column <code>mini_shop_471752.b2c_distributor_apply.id</code>.
     */
    public final TableField<DistributorApplyRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_distributor_apply.user_id</code>.
     */
    public final TableField<DistributorApplyRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_distributor_apply.status</code>.
     */
    public final TableField<DistributorApplyRecord, Byte> STATUS = createField("status", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_distributor_apply.msg</code>. 审核内容
     */
    public final TableField<DistributorApplyRecord, String> MSG = createField("msg", org.jooq.impl.SQLDataType.CLOB, this, "审核内容");

    /**
     * The column <code>mini_shop_471752.b2c_distributor_apply.del_flag</code>.
     */
    public final TableField<DistributorApplyRecord, Byte> DEL_FLAG = createField("del_flag", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_distributor_apply.activation_fields</code>. 审核校验
     */
    public final TableField<DistributorApplyRecord, String> ACTIVATION_FIELDS = createField("activation_fields", org.jooq.impl.SQLDataType.CLOB, this, "审核校验");

    /**
     * The column <code>mini_shop_471752.b2c_distributor_apply.config_fields</code>. 审核字段
     */
    public final TableField<DistributorApplyRecord, String> CONFIG_FIELDS = createField("config_fields", org.jooq.impl.SQLDataType.VARCHAR(500), this, "审核字段");

    /**
     * The column <code>mini_shop_471752.b2c_distributor_apply.create_time</code>.
     */
    public final TableField<DistributorApplyRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_distributor_apply.update_time</code>. 最后修改时间
     */
    public final TableField<DistributorApplyRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "最后修改时间");

    /**
     * The column <code>mini_shop_471752.b2c_distributor_apply.is_auto_pass</code>. 审核类型 0：手动审核；1"自动审核
     */
    public final TableField<DistributorApplyRecord, Byte> IS_AUTO_PASS = createField("is_auto_pass", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "审核类型 0：手动审核；1\"自动审核");

    /**
     * Create a <code>mini_shop_471752.b2c_distributor_apply</code> table reference
     */
    public DistributorApply() {
        this(DSL.name("b2c_distributor_apply"), null);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_distributor_apply</code> table reference
     */
    public DistributorApply(String alias) {
        this(DSL.name(alias), DISTRIBUTOR_APPLY);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_distributor_apply</code> table reference
     */
    public DistributorApply(Name alias) {
        this(alias, DISTRIBUTOR_APPLY);
    }

    private DistributorApply(Name alias, Table<DistributorApplyRecord> aliased) {
        this(alias, aliased, null);
    }

    private DistributorApply(Name alias, Table<DistributorApplyRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("分销原申请记录"));
    }

    public <O extends Record> DistributorApply(Table<O> child, ForeignKey<O, DistributorApplyRecord> key) {
        super(child, key, DISTRIBUTOR_APPLY);
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
        return Arrays.<Index>asList(Indexes.DISTRIBUTOR_APPLY_PRIMARY, Indexes.DISTRIBUTOR_APPLY_USER_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<DistributorApplyRecord, Integer> getIdentity() {
        return Keys.IDENTITY_DISTRIBUTOR_APPLY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<DistributorApplyRecord> getPrimaryKey() {
        return Keys.KEY_B2C_DISTRIBUTOR_APPLY_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<DistributorApplyRecord>> getKeys() {
        return Arrays.<UniqueKey<DistributorApplyRecord>>asList(Keys.KEY_B2C_DISTRIBUTOR_APPLY_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributorApply as(String alias) {
        return new DistributorApply(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributorApply as(Name alias) {
        return new DistributorApply(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DistributorApply rename(String name) {
        return new DistributorApply(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DistributorApply rename(Name name) {
        return new DistributorApply(name, null);
    }
}
