/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.PageClassificationRecord;

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
public class PageClassification extends TableImpl<PageClassificationRecord> {

    private static final long serialVersionUID = -1304187637;

    /**
     * The reference instance of <code>mini_shop_471752.b2c_page_classification</code>
     */
    public static final PageClassification PAGE_CLASSIFICATION = new PageClassification();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PageClassificationRecord> getRecordType() {
        return PageClassificationRecord.class;
    }

    /**
     * The column <code>mini_shop_471752.b2c_page_classification.id</code>. id
     */
    public final TableField<PageClassificationRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "id");

    /**
     * The column <code>mini_shop_471752.b2c_page_classification.shop_id</code>. 店铺id
     */
    public final TableField<PageClassificationRecord, Integer> SHOP_ID = createField("shop_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "店铺id");

    /**
     * The column <code>mini_shop_471752.b2c_page_classification.name</code>. 分类名称
     */
    public final TableField<PageClassificationRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR(60).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "分类名称");

    /**
     * The column <code>mini_shop_471752.b2c_page_classification.create_time</code>.
     */
    public final TableField<PageClassificationRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_page_classification.update_time</code>. 最后修改时间
     */
    public final TableField<PageClassificationRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "最后修改时间");

    /**
     * Create a <code>mini_shop_471752.b2c_page_classification</code> table reference
     */
    public PageClassification() {
        this(DSL.name("b2c_page_classification"), null);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_page_classification</code> table reference
     */
    public PageClassification(String alias) {
        this(DSL.name(alias), PAGE_CLASSIFICATION);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_page_classification</code> table reference
     */
    public PageClassification(Name alias) {
        this(alias, PAGE_CLASSIFICATION);
    }

    private PageClassification(Name alias, Table<PageClassificationRecord> aliased) {
        this(alias, aliased, null);
    }

    private PageClassification(Name alias, Table<PageClassificationRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> PageClassification(Table<O> child, ForeignKey<O, PageClassificationRecord> key) {
        super(child, key, PAGE_CLASSIFICATION);
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
        return Arrays.<Index>asList(Indexes.PAGE_CLASSIFICATION_PRIMARY, Indexes.PAGE_CLASSIFICATION_SHOP_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<PageClassificationRecord, Integer> getIdentity() {
        return Keys.IDENTITY_PAGE_CLASSIFICATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<PageClassificationRecord> getPrimaryKey() {
        return Keys.KEY_B2C_PAGE_CLASSIFICATION_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PageClassificationRecord>> getKeys() {
        return Arrays.<UniqueKey<PageClassificationRecord>>asList(Keys.KEY_B2C_PAGE_CLASSIFICATION_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageClassification as(String alias) {
        return new PageClassification(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageClassification as(Name alias) {
        return new PageClassification(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PageClassification rename(String name) {
        return new PageClassification(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PageClassification rename(Name name) {
        return new PageClassification(name, null);
    }
}
