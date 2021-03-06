/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.ArticleRecord;

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
public class Article extends TableImpl<ArticleRecord> {

    private static final long serialVersionUID = -712063006;

    /**
     * The reference instance of <code>mini_shop_471752.b2c_article</code>
     */
    public static final Article ARTICLE = new Article();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ArticleRecord> getRecordType() {
        return ArticleRecord.class;
    }

    /**
     * The column <code>mini_shop_471752.b2c_article.article_id</code>.
     */
    public final TableField<ArticleRecord, Integer> ARTICLE_ID = createField("article_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_article.category_id</code>. 文章分类
     */
    public final TableField<ArticleRecord, Integer> CATEGORY_ID = createField("category_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("1", org.jooq.impl.SQLDataType.INTEGER)), this, "文章分类");

    /**
     * The column <code>mini_shop_471752.b2c_article.title</code>.
     */
    public final TableField<ArticleRecord, String> TITLE = createField("title", org.jooq.impl.SQLDataType.VARCHAR(256), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_article.author</code>.
     */
    public final TableField<ArticleRecord, String> AUTHOR = createField("author", org.jooq.impl.SQLDataType.VARCHAR(50), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_article.keyword</code>. 标签
     */
    public final TableField<ArticleRecord, String> KEYWORD = createField("keyword", org.jooq.impl.SQLDataType.VARCHAR(256), this, "标签");

    /**
     * The column <code>mini_shop_471752.b2c_article.desc</code>. 文章描述
     */
    public final TableField<ArticleRecord, String> DESC = createField("desc", org.jooq.impl.SQLDataType.VARCHAR(1024), this, "文章描述");

    /**
     * The column <code>mini_shop_471752.b2c_article.content</code>.
     */
    public final TableField<ArticleRecord, String> CONTENT = createField("content", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>mini_shop_471752.b2c_article.is_recommend</code>. 1:推荐
     */
    public final TableField<ArticleRecord, Byte> IS_RECOMMEND = createField("is_recommend", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "1:推荐");

    /**
     * The column <code>mini_shop_471752.b2c_article.is_top</code>. 1:置顶
     */
    public final TableField<ArticleRecord, Byte> IS_TOP = createField("is_top", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "1:置顶");

    /**
     * The column <code>mini_shop_471752.b2c_article.status</code>. 0未发布,1已发布
     */
    public final TableField<ArticleRecord, Byte> STATUS = createField("status", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "0未发布,1已发布");

    /**
     * The column <code>mini_shop_471752.b2c_article.pub_time</code>. 发布时间
     */
    public final TableField<ArticleRecord, Timestamp> PUB_TIME = createField("pub_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "发布时间");

    /**
     * The column <code>mini_shop_471752.b2c_article.update_time</code>. 更新时间
     */
    public final TableField<ArticleRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "更新时间");

    /**
     * The column <code>mini_shop_471752.b2c_article.create_time</code>.
     */
    public final TableField<ArticleRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_article.last_visit_time</code>.
     */
    public final TableField<ArticleRecord, Timestamp> LAST_VISIT_TIME = createField("last_visit_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>mini_shop_471752.b2c_article.pv</code>.
     */
    public final TableField<ArticleRecord, Integer> PV = createField("pv", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>mini_shop_471752.b2c_article.show_footer</code>. 0:不在footer显示，1：显示
     */
    public final TableField<ArticleRecord, Byte> SHOW_FOOTER = createField("show_footer", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "0:不在footer显示，1：显示");

    /**
     * The column <code>mini_shop_471752.b2c_article.part_type</code>. 文章所属类型：0普通，1门店公告类文章
     */
    public final TableField<ArticleRecord, Byte> PART_TYPE = createField("part_type", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "文章所属类型：0普通，1门店公告类文章");

    /**
     * The column <code>mini_shop_471752.b2c_article.cover_img</code>. 封面图片路径
     */
    public final TableField<ArticleRecord, String> COVER_IMG = createField("cover_img", org.jooq.impl.SQLDataType.VARCHAR(50), this, "封面图片路径");

    /**
     * The column <code>mini_shop_471752.b2c_article.is_del</code>. 0未删除,1已删除
     */
    public final TableField<ArticleRecord, Byte> IS_DEL = createField("is_del", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "0未删除,1已删除");

    /**
     * Create a <code>mini_shop_471752.b2c_article</code> table reference
     */
    public Article() {
        this(DSL.name("b2c_article"), null);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_article</code> table reference
     */
    public Article(String alias) {
        this(DSL.name(alias), ARTICLE);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_article</code> table reference
     */
    public Article(Name alias) {
        this(alias, ARTICLE);
    }

    private Article(Name alias, Table<ArticleRecord> aliased) {
        this(alias, aliased, null);
    }

    private Article(Name alias, Table<ArticleRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Article(Table<O> child, ForeignKey<O, ArticleRecord> key) {
        super(child, key, ARTICLE);
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
        return Arrays.<Index>asList(Indexes.ARTICLE_IS_RECOMMEND, Indexes.ARTICLE_IS_TOP, Indexes.ARTICLE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<ArticleRecord, Integer> getIdentity() {
        return Keys.IDENTITY_ARTICLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ArticleRecord> getPrimaryKey() {
        return Keys.KEY_B2C_ARTICLE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ArticleRecord>> getKeys() {
        return Arrays.<UniqueKey<ArticleRecord>>asList(Keys.KEY_B2C_ARTICLE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Article as(String alias) {
        return new Article(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Article as(Name alias) {
        return new Article(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Article rename(String name) {
        return new Article(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Article rename(Name name) {
        return new Article(name, null);
    }
}
