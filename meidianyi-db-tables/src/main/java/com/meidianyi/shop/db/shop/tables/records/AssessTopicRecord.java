/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.AssessTopic;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record17;
import org.jooq.Row17;
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
public class AssessTopicRecord extends UpdatableRecordImpl<AssessTopicRecord> implements Record17<Integer, Integer, Integer, Byte, String, String, Byte, Byte, String, Byte, Byte, Integer, String, Integer, Timestamp, Timestamp, Byte> {

    private static final long serialVersionUID = -649508830;

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.shop_id</code>. 店铺ID
     */
    public void setShopId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.shop_id</code>. 店铺ID
     */
    public Integer getShopId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.assess_id</code>. 测评活动ID
     */
    public void setAssessId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.assess_id</code>. 测评活动ID
     */
    public Integer getAssessId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.topic_type</code>. 题目格式：0文本，1图片，2视频
     */
    public void setTopicType(Byte value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.topic_type</code>. 题目格式：0文本，1图片，2视频
     */
    public Byte getTopicType() {
        return (Byte) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.topic_type_path</code>. 题目图片、视频路径
     */
    public void setTopicTypePath(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.topic_type_path</code>. 题目图片、视频路径
     */
    public String getTopicTypePath() {
        return (String) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.topic_title</code>. 题目标题
     */
    public void setTopicTitle(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.topic_title</code>. 题目标题
     */
    public String getTopicTitle() {
        return (String) get(5);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.topic_level</code>. 题目优先级，越小优先级越大，从1开始
     */
    public void setTopicLevel(Byte value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.topic_level</code>. 题目优先级，越小优先级越大，从1开始
     */
    public Byte getTopicLevel() {
        return (Byte) get(6);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.bg_img_type</code>. 题目背景类型：0默认，1自定义
     */
    public void setBgImgType(Byte value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.bg_img_type</code>. 题目背景类型：0默认，1自定义
     */
    public Byte getBgImgType() {
        return (Byte) get(7);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.bg_img_path</code>. 题目背景图片路径
     */
    public void setBgImgPath(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.bg_img_path</code>. 题目背景图片路径
     */
    public String getBgImgPath() {
        return (String) get(8);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.option_type</code>. 选项类型：0单选，1多选
     */
    public void setOptionType(Byte value) {
        set(9, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.option_type</code>. 选项类型：0单选，1多选
     */
    public Byte getOptionType() {
        return (Byte) get(9);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.option_skip_type</code>. 多选时跳转类型：1跳转到指定题目，2跳转导致指定结果
     */
    public void setOptionSkipType(Byte value) {
        set(10, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.option_skip_type</code>. 多选时跳转类型：1跳转到指定题目，2跳转导致指定结果
     */
    public Byte getOptionSkipType() {
        return (Byte) get(10);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.option_skip_value</code>. 多选时跳转到指定题目ID或者结果ID
     */
    public void setOptionSkipValue(Integer value) {
        set(11, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.option_skip_value</code>. 多选时跳转到指定题目ID或者结果ID
     */
    public Integer getOptionSkipValue() {
        return (Integer) get(11);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.option_content</code>. 选项内容，json串，包括选项描述、图片和跳转逻辑或者分值
     */
    public void setOptionContent(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.option_content</code>. 选项内容，json串，包括选项描述、图片和跳转逻辑或者分值
     */
    public String getOptionContent() {
        return (String) get(12);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.result_id</code>. 对应ID
     */
    public void setResultId(Integer value) {
        set(13, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.result_id</code>. 对应ID
     */
    public Integer getResultId() {
        return (Integer) get(13);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.create_time</code>.
     */
    public void setCreateTime(Timestamp value) {
        set(14, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.create_time</code>.
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(14);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.update_time</code>. 最后修改时间
     */
    public void setUpdateTime(Timestamp value) {
        set(15, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.update_time</code>. 最后修改时间
     */
    public Timestamp getUpdateTime() {
        return (Timestamp) get(15);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_assess_topic.del_flag</code>. 删除标识：0未删除，1已删除
     */
    public void setDelFlag(Byte value) {
        set(16, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_assess_topic.del_flag</code>. 删除标识：0未删除，1已删除
     */
    public Byte getDelFlag() {
        return (Byte) get(16);
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
    // Record17 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row17<Integer, Integer, Integer, Byte, String, String, Byte, Byte, String, Byte, Byte, Integer, String, Integer, Timestamp, Timestamp, Byte> fieldsRow() {
        return (Row17) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row17<Integer, Integer, Integer, Byte, String, String, Byte, Byte, String, Byte, Byte, Integer, String, Integer, Timestamp, Timestamp, Byte> valuesRow() {
        return (Row17) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return AssessTopic.ASSESS_TOPIC.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return AssessTopic.ASSESS_TOPIC.SHOP_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return AssessTopic.ASSESS_TOPIC.ASSESS_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field4() {
        return AssessTopic.ASSESS_TOPIC.TOPIC_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return AssessTopic.ASSESS_TOPIC.TOPIC_TYPE_PATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return AssessTopic.ASSESS_TOPIC.TOPIC_TITLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field7() {
        return AssessTopic.ASSESS_TOPIC.TOPIC_LEVEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field8() {
        return AssessTopic.ASSESS_TOPIC.BG_IMG_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return AssessTopic.ASSESS_TOPIC.BG_IMG_PATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field10() {
        return AssessTopic.ASSESS_TOPIC.OPTION_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field11() {
        return AssessTopic.ASSESS_TOPIC.OPTION_SKIP_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field12() {
        return AssessTopic.ASSESS_TOPIC.OPTION_SKIP_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return AssessTopic.ASSESS_TOPIC.OPTION_CONTENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field14() {
        return AssessTopic.ASSESS_TOPIC.RESULT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field15() {
        return AssessTopic.ASSESS_TOPIC.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field16() {
        return AssessTopic.ASSESS_TOPIC.UPDATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field17() {
        return AssessTopic.ASSESS_TOPIC.DEL_FLAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component2() {
        return getShopId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component3() {
        return getAssessId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component4() {
        return getTopicType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getTopicTypePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getTopicTitle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component7() {
        return getTopicLevel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component8() {
        return getBgImgType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component9() {
        return getBgImgPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component10() {
        return getOptionType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component11() {
        return getOptionSkipType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component12() {
        return getOptionSkipValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component13() {
        return getOptionContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component14() {
        return getResultId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component15() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component16() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component17() {
        return getDelFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getShopId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getAssessId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value4() {
        return getTopicType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getTopicTypePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getTopicTitle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value7() {
        return getTopicLevel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value8() {
        return getBgImgType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getBgImgPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value10() {
        return getOptionType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value11() {
        return getOptionSkipType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value12() {
        return getOptionSkipValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getOptionContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value14() {
        return getResultId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value15() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value16() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value17() {
        return getDelFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value2(Integer value) {
        setShopId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value3(Integer value) {
        setAssessId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value4(Byte value) {
        setTopicType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value5(String value) {
        setTopicTypePath(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value6(String value) {
        setTopicTitle(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value7(Byte value) {
        setTopicLevel(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value8(Byte value) {
        setBgImgType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value9(String value) {
        setBgImgPath(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value10(Byte value) {
        setOptionType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value11(Byte value) {
        setOptionSkipType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value12(Integer value) {
        setOptionSkipValue(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value13(String value) {
        setOptionContent(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value14(Integer value) {
        setResultId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value15(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value16(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord value17(Byte value) {
        setDelFlag(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssessTopicRecord values(Integer value1, Integer value2, Integer value3, Byte value4, String value5, String value6, Byte value7, Byte value8, String value9, Byte value10, Byte value11, Integer value12, String value13, Integer value14, Timestamp value15, Timestamp value16, Byte value17) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AssessTopicRecord
     */
    public AssessTopicRecord() {
        super(AssessTopic.ASSESS_TOPIC);
    }

    /**
     * Create a detached, initialised AssessTopicRecord
     */
    public AssessTopicRecord(Integer id, Integer shopId, Integer assessId, Byte topicType, String topicTypePath, String topicTitle, Byte topicLevel, Byte bgImgType, String bgImgPath, Byte optionType, Byte optionSkipType, Integer optionSkipValue, String optionContent, Integer resultId, Timestamp createTime, Timestamp updateTime, Byte delFlag) {
        super(AssessTopic.ASSESS_TOPIC);

        set(0, id);
        set(1, shopId);
        set(2, assessId);
        set(3, topicType);
        set(4, topicTypePath);
        set(5, topicTitle);
        set(6, topicLevel);
        set(7, bgImgType);
        set(8, bgImgPath);
        set(9, optionType);
        set(10, optionSkipType);
        set(11, optionSkipValue);
        set(12, optionContent);
        set(13, resultId);
        set(14, createTime);
        set(15, updateTime);
        set(16, delFlag);
    }
}