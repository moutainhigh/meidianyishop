/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.main.tables;


import com.meidianyi.shop.db.main.Indexes;
import com.meidianyi.shop.db.main.Keys;
import com.meidianyi.shop.db.main.MiniMain;
import com.meidianyi.shop.db.main.tables.records.SpecRecord;

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
public class Spec extends TableImpl<SpecRecord> {

    private static final long serialVersionUID = 896806908;

    /**
     * The reference instance of <code>mini_main.b2c_spec</code>
     */
    public static final Spec SPEC = new Spec();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SpecRecord> getRecordType() {
        return SpecRecord.class;
    }

    /**
     * The column <code>mini_main.b2c_spec.spec_id</code>.
     */
    public final TableField<SpecRecord, Integer> SPEC_ID = createField("spec_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>mini_main.b2c_spec.spec_name</code>.
     */
    public final TableField<SpecRecord, String> SPEC_NAME = createField("spec_name", org.jooq.impl.SQLDataType.VARCHAR(60).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>mini_main.b2c_spec.del_flag</code>.
     */
    public final TableField<SpecRecord, Byte> DEL_FLAG = createField("del_flag", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>mini_main.b2c_spec.shop_id</code>. 店铺ID
     */
    public final TableField<SpecRecord, Integer> SHOP_ID = createField("shop_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "店铺ID");

    /**
     * Create a <code>mini_main.b2c_spec</code> table reference
     */
    public Spec() {
        this(DSL.name("b2c_spec"), null);
    }

    /**
     * Create an aliased <code>mini_main.b2c_spec</code> table reference
     */
    public Spec(String alias) {
        this(DSL.name(alias), SPEC);
    }

    /**
     * Create an aliased <code>mini_main.b2c_spec</code> table reference
     */
    public Spec(Name alias) {
        this(alias, SPEC);
    }

    private Spec(Name alias, Table<SpecRecord> aliased) {
        this(alias, aliased, null);
    }

    private Spec(Name alias, Table<SpecRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Spec(Table<O> child, ForeignKey<O, SpecRecord> key) {
        super(child, key, SPEC);
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
        return Arrays.<Index>asList(Indexes.SPEC_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<SpecRecord, Integer> getIdentity() {
        return Keys.IDENTITY_SPEC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SpecRecord> getPrimaryKey() {
        return Keys.KEY_B2C_SPEC_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SpecRecord>> getKeys() {
        return Arrays.<UniqueKey<SpecRecord>>asList(Keys.KEY_B2C_SPEC_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Spec as(String alias) {
        return new Spec(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Spec as(Name alias) {
        return new Spec(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Spec rename(String name) {
        return new Spec(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Spec rename(Name name) {
        return new Spec(name, null);
    }
}
