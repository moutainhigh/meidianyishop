/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.Article;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record19;
import org.jooq.Row19;
import org.jooq.impl.UpdatableRecordImpl;


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
public class ArticleRecord extends UpdatableRecordImpl<ArticleRecord> implements Record19<Integer, Integer, String, String, String, String, String, Byte, Byte, Byte, Timestamp, Timestamp, Timestamp, Timestamp, Integer, Byte, Byte, String, Byte> {

    private static final long serialVersionUID = -261106434;

    /**
     * Setter for <code>mini_shop_471752.b2c_article.article_id</code>.
     */
    public void setArticleId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.article_id</code>.
     */
    public Integer getArticleId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.category_id</code>. 文章分类
     */
    public void setCategoryId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.category_id</code>. 文章分类
     */
    public Integer getCategoryId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.title</code>.
     */
    public void setTitle(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.title</code>.
     */
    public String getTitle() {
        return (String) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.author</code>.
     */
    public void setAuthor(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.author</code>.
     */
    public String getAuthor() {
        return (String) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.keyword</code>. 标签
     */
    public void setKeyword(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.keyword</code>. 标签
     */
    public String getKeyword() {
        return (String) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.desc</code>. 文章描述
     */
    public void setDesc(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.desc</code>. 文章描述
     */
    public String getDesc() {
        return (String) get(5);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.content</code>.
     */
    public void setContent(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.content</code>.
     */
    public String getContent() {
        return (String) get(6);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.is_recommend</code>. 1:推荐
     */
    public void setIsRecommend(Byte value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.is_recommend</code>. 1:推荐
     */
    public Byte getIsRecommend() {
        return (Byte) get(7);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.is_top</code>. 1:置顶
     */
    public void setIsTop(Byte value) {
        set(8, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.is_top</code>. 1:置顶
     */
    public Byte getIsTop() {
        return (Byte) get(8);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.status</code>. 0未发布,1已发布
     */
    public void setStatus(Byte value) {
        set(9, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.status</code>. 0未发布,1已发布
     */
    public Byte getStatus() {
        return (Byte) get(9);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.pub_time</code>. 发布时间
     */
    public void setPubTime(Timestamp value) {
        set(10, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.pub_time</code>. 发布时间
     */
    public Timestamp getPubTime() {
        return (Timestamp) get(10);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.update_time</code>. 更新时间
     */
    public void setUpdateTime(Timestamp value) {
        set(11, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.update_time</code>. 更新时间
     */
    public Timestamp getUpdateTime() {
        return (Timestamp) get(11);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.create_time</code>.
     */
    public void setCreateTime(Timestamp value) {
        set(12, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.create_time</code>.
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(12);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.last_visit_time</code>.
     */
    public void setLastVisitTime(Timestamp value) {
        set(13, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.last_visit_time</code>.
     */
    public Timestamp getLastVisitTime() {
        return (Timestamp) get(13);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.pv</code>.
     */
    public void setPv(Integer value) {
        set(14, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.pv</code>.
     */
    public Integer getPv() {
        return (Integer) get(14);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.show_footer</code>. 0:不在footer显示，1：显示
     */
    public void setShowFooter(Byte value) {
        set(15, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.show_footer</code>. 0:不在footer显示，1：显示
     */
    public Byte getShowFooter() {
        return (Byte) get(15);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.part_type</code>. 文章所属类型：0普通，1门店公告类文章
     */
    public void setPartType(Byte value) {
        set(16, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.part_type</code>. 文章所属类型：0普通，1门店公告类文章
     */
    public Byte getPartType() {
        return (Byte) get(16);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.cover_img</code>. 封面图片路径
     */
    public void setCoverImg(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.cover_img</code>. 封面图片路径
     */
    public String getCoverImg() {
        return (String) get(17);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_article.is_del</code>. 0未删除,1已删除
     */
    public void setIsDel(Byte value) {
        set(18, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_article.is_del</code>. 0未删除,1已删除
     */
    public Byte getIsDel() {
        return (Byte) get(18);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record19 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row19<Integer, Integer, String, String, String, String, String, Byte, Byte, Byte, Timestamp, Timestamp, Timestamp, Timestamp, Integer, Byte, Byte, String, Byte> fieldsRow() {
        return (Row19) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row19<Integer, Integer, String, String, String, String, String, Byte, Byte, Byte, Timestamp, Timestamp, Timestamp, Timestamp, Integer, Byte, Byte, String, Byte> valuesRow() {
        return (Row19) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Article.ARTICLE.ARTICLE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return Article.ARTICLE.CATEGORY_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Article.ARTICLE.TITLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Article.ARTICLE.AUTHOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Article.ARTICLE.KEYWORD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Article.ARTICLE.DESC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Article.ARTICLE.CONTENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field8() {
        return Article.ARTICLE.IS_RECOMMEND;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field9() {
        return Article.ARTICLE.IS_TOP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field10() {
        return Article.ARTICLE.STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field11() {
        return Article.ARTICLE.PUB_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field12() {
        return Article.ARTICLE.UPDATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field13() {
        return Article.ARTICLE.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field14() {
        return Article.ARTICLE.LAST_VISIT_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field15() {
        return Article.ARTICLE.PV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field16() {
        return Article.ARTICLE.SHOW_FOOTER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field17() {
        return Article.ARTICLE.PART_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field18() {
        return Article.ARTICLE.COVER_IMG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field19() {
        return Article.ARTICLE.IS_DEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getArticleId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component2() {
        return getCategoryId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getTitle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getAuthor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getKeyword();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getDesc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component7() {
        return getContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component8() {
        return getIsRecommend();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component9() {
        return getIsTop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component10() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component11() {
        return getPubTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component12() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component13() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component14() {
        return getLastVisitTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component15() {
        return getPv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component16() {
        return getShowFooter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component17() {
        return getPartType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component18() {
        return getCoverImg();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component19() {
        return getIsDel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getArticleId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getCategoryId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getTitle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getAuthor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getKeyword();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getDesc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value8() {
        return getIsRecommend();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value9() {
        return getIsTop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value10() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value11() {
        return getPubTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value12() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value13() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value14() {
        return getLastVisitTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value15() {
        return getPv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value16() {
        return getShowFooter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value17() {
        return getPartType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value18() {
        return getCoverImg();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value19() {
        return getIsDel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value1(Integer value) {
        setArticleId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value2(Integer value) {
        setCategoryId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value3(String value) {
        setTitle(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value4(String value) {
        setAuthor(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value5(String value) {
        setKeyword(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value6(String value) {
        setDesc(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value7(String value) {
        setContent(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value8(Byte value) {
        setIsRecommend(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value9(Byte value) {
        setIsTop(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value10(Byte value) {
        setStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value11(Timestamp value) {
        setPubTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value12(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value13(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value14(Timestamp value) {
        setLastVisitTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value15(Integer value) {
        setPv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value16(Byte value) {
        setShowFooter(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value17(Byte value) {
        setPartType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value18(String value) {
        setCoverImg(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord value19(Byte value) {
        setIsDel(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArticleRecord values(Integer value1, Integer value2, String value3, String value4, String value5, String value6, String value7, Byte value8, Byte value9, Byte value10, Timestamp value11, Timestamp value12, Timestamp value13, Timestamp value14, Integer value15, Byte value16, Byte value17, String value18, Byte value19) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        value17(value17);
        value18(value18);
        value19(value19);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ArticleRecord
     */
    public ArticleRecord() {
        super(Article.ARTICLE);
    }

    /**
     * Create a detached, initialised ArticleRecord
     */
    public ArticleRecord(Integer articleId, Integer categoryId, String title, String author, String keyword, String desc, String content, Byte isRecommend, Byte isTop, Byte status, Timestamp pubTime, Timestamp updateTime, Timestamp createTime, Timestamp lastVisitTime, Integer pv, Byte showFooter, Byte partType, String coverImg, Byte isDel) {
        super(Article.ARTICLE);

        set(0, articleId);
        set(1, categoryId);
        set(2, title);
        set(3, author);
        set(4, keyword);
        set(5, desc);
        set(6, content);
        set(7, isRecommend);
        set(8, isTop);
        set(9, status);
        set(10, pubTime);
        set(11, updateTime);
        set(12, createTime);
        set(13, lastVisitTime);
        set(14, pv);
        set(15, showFooter);
        set(16, partType);
        set(17, coverImg);
        set(18, isDel);
    }
}
