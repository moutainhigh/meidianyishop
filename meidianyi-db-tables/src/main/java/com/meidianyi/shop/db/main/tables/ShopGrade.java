/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.main.tables;


import com.meidianyi.shop.db.main.Indexes;
import com.meidianyi.shop.db.main.Keys;
import com.meidianyi.shop.db.main.MiniMain;
import com.meidianyi.shop.db.main.tables.records.ShopGradeRecord;

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
public class ShopGrade extends TableImpl<ShopGradeRecord> {

    private static final long serialVersionUID = 1179745153;

    /**
     * The reference instance of <code>mini_main.b2c_shop_grade</code>
     */
    public static final ShopGrade SHOP_GRADE = new ShopGrade();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ShopGradeRecord> getRecordType() {
        return ShopGradeRecord.class;
    }

    /**
     * The column <code>mini_main.b2c_shop_grade.id</code>.
     */
    public final TableField<ShopGradeRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>mini_main.b2c_shop_grade.shop_grade</code>. 店铺等级
     */
    public final TableField<ShopGradeRecord, String> SHOP_GRADE_ = createField("shop_grade", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "店铺等级");

    /**
     * The column <code>mini_main.b2c_shop_grade.max_sku_num</code>. SKU数量
     */
    public final TableField<ShopGradeRecord, Integer> MAX_SKU_NUM = createField("max_sku_num", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "SKU数量");

    /**
     * The column <code>mini_main.b2c_shop_grade.max_shop_num</code>. 店铺数量
     */
    public final TableField<ShopGradeRecord, Integer> MAX_SHOP_NUM = createField("max_shop_num", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "店铺数量");

    /**
     * The column <code>mini_main.b2c_shop_grade.manage_fee</code>. 平台管理费百分比
     */
    public final TableField<ShopGradeRecord, BigDecimal> MANAGE_FEE = createField("manage_fee", org.jooq.impl.SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(org.jooq.impl.DSL.inline("0.00", org.jooq.impl.SQLDataType.DECIMAL)), this, "平台管理费百分比");

    /**
     * The column <code>mini_main.b2c_shop_grade.flag</code>. 0:正常，1:删除
     */
    public final TableField<ShopGradeRecord, Byte> FLAG = createField("flag", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "0:正常，1:删除");

    /**
     * The column <code>mini_main.b2c_shop_grade.in_time</code>.
     */
    public final TableField<ShopGradeRecord, Timestamp> IN_TIME = createField("in_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>mini_main.b2c_shop_grade.up_time</code>.
     */
    public final TableField<ShopGradeRecord, Timestamp> UP_TIME = createField("up_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * Create a <code>mini_main.b2c_shop_grade</code> table reference
     */
    public ShopGrade() {
        this(DSL.name("b2c_shop_grade"), null);
    }

    /**
     * Create an aliased <code>mini_main.b2c_shop_grade</code> table reference
     */
    public ShopGrade(String alias) {
        this(DSL.name(alias), SHOP_GRADE);
    }

    /**
     * Create an aliased <code>mini_main.b2c_shop_grade</code> table reference
     */
    public ShopGrade(Name alias) {
        this(alias, SHOP_GRADE);
    }

    private ShopGrade(Name alias, Table<ShopGradeRecord> aliased) {
        this(alias, aliased, null);
    }

    private ShopGrade(Name alias, Table<ShopGradeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> ShopGrade(Table<O> child, ForeignKey<O, ShopGradeRecord> key) {
        super(child, key, SHOP_GRADE);
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
        return Arrays.<Index>asList(Indexes.SHOP_GRADE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<ShopGradeRecord, Integer> getIdentity() {
        return Keys.IDENTITY_SHOP_GRADE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ShopGradeRecord> getPrimaryKey() {
        return Keys.KEY_B2C_SHOP_GRADE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ShopGradeRecord>> getKeys() {
        return Arrays.<UniqueKey<ShopGradeRecord>>asList(Keys.KEY_B2C_SHOP_GRADE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopGrade as(String alias) {
        return new ShopGrade(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopGrade as(Name alias) {
        return new ShopGrade(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ShopGrade rename(String name) {
        return new ShopGrade(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ShopGrade rename(Name name) {
        return new ShopGrade(name, null);
    }
}