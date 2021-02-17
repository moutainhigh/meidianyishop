/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.Code;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;
import java.sql.Timestamp;


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
public class CodeRecord extends UpdatableRecordImpl<CodeRecord> implements Record9<Integer, Short, String, String, String, Byte, String, Timestamp, Timestamp> {

    private static final long serialVersionUID = -206351930;

    /**
     * Setter for <code>mini_shop_471752.b2c_code.code_id</code>. 二维码id
     */
    public void setCodeId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_code.code_id</code>. 二维码id
     */
    public Integer getCodeId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_code.type</code>. 分类：1店铺，2商品，3服务，4会员卡，5优惠券
     */
    public void setType(Short value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_code.type</code>. 分类：1店铺，2商品，3服务，4会员卡，5优惠券
     */
    public Short getType() {
        return (Short) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_code.param_id</code>. 对应的记录唯一值，可根据url产生
     */
    public void setParamId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_code.param_id</code>. 对应的记录唯一值，可根据url产生
     */
    public String getParamId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_code.type_url</code>. type对应的app页面地址
     */
    public void setTypeUrl(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_code.type_url</code>. type对应的app页面地址
     */
    public String getTypeUrl() {
        return (String) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_code.qrcode_img</code>. 二维码位置
     */
    public void setQrcodeImg(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_code.qrcode_img</code>. 二维码位置
     */
    public String getQrcodeImg() {
        return (String) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_code.del_flag</code>. 标记位
     */
    public void setDelFlag(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_code.del_flag</code>. 标记位
     */
    public Byte getDelFlag() {
        return (Byte) get(5);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_code.channel</code>. 渠道分享码，保留字段目前未使用，后期确认后可删除
     */
    public void setChannel(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_code.channel</code>. 渠道分享码，保留字段目前未使用，后期确认后可删除
     */
    public String getChannel() {
        return (String) get(6);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_code.create_time</code>.
     */
    public void setCreateTime(Timestamp value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_code.create_time</code>.
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(7);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_code.update_time</code>. 最后修改时间
     */
    public void setUpdateTime(Timestamp value) {
        set(8, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_code.update_time</code>. 最后修改时间
     */
    public Timestamp getUpdateTime() {
        return (Timestamp) get(8);
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
    // Record9 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row9<Integer, Short, String, String, String, Byte, String, Timestamp, Timestamp> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row9<Integer, Short, String, String, String, Byte, String, Timestamp, Timestamp> valuesRow() {
        return (Row9) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Code.CODE.CODE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field2() {
        return Code.CODE.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Code.CODE.PARAM_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Code.CODE.TYPE_URL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Code.CODE.QRCODE_IMG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field6() {
        return Code.CODE.DEL_FLAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Code.CODE.CHANNEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field8() {
        return Code.CODE.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field9() {
        return Code.CODE.UPDATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getCodeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short component2() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getParamId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getTypeUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getQrcodeImg();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component6() {
        return getDelFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component7() {
        return getChannel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component8() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component9() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getCodeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value2() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getParamId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getTypeUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getQrcodeImg();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value6() {
        return getDelFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getChannel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value8() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value9() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeRecord value1(Integer value) {
        setCodeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeRecord value2(Short value) {
        setType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeRecord value3(String value) {
        setParamId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeRecord value4(String value) {
        setTypeUrl(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeRecord value5(String value) {
        setQrcodeImg(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeRecord value6(Byte value) {
        setDelFlag(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeRecord value7(String value) {
        setChannel(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeRecord value8(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeRecord value9(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeRecord values(Integer value1, Short value2, String value3, String value4, String value5, Byte value6, String value7, Timestamp value8, Timestamp value9) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CodeRecord
     */
    public CodeRecord() {
        super(Code.CODE);
    }

    /**
     * Create a detached, initialised CodeRecord
     */
    public CodeRecord(Integer codeId, Short type, String paramId, String typeUrl, String qrcodeImg, Byte delFlag, String channel, Timestamp createTime, Timestamp updateTime) {
        super(Code.CODE);

        set(0, codeId);
        set(1, type);
        set(2, paramId);
        set(3, typeUrl);
        set(4, qrcodeImg);
        set(5, delFlag);
        set(6, channel);
        set(7, createTime);
        set(8, updateTime);
    }
}