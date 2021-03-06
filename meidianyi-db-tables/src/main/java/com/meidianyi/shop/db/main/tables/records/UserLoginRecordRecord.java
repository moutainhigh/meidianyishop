/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.main.tables.records;


import com.meidianyi.shop.db.main.tables.UserLoginRecord;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
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
public class UserLoginRecordRecord extends UpdatableRecordImpl<UserLoginRecordRecord> implements Record10<Integer, Integer, String, Integer, Integer, String, Timestamp, String, Short, Byte> {

    private static final long serialVersionUID = 157660650;

    /**
     * Setter for <code>mini_main.b2c_user_login_record.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_main.b2c_user_login_record.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_main.b2c_user_login_record.shop_id</code>. 店铺ID
     */
    public void setShopId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_main.b2c_user_login_record.shop_id</code>. 店铺ID
     */
    public Integer getShopId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>mini_main.b2c_user_login_record.shop_name</code>. 店铺名称
     */
    public void setShopName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_main.b2c_user_login_record.shop_name</code>. 店铺名称
     */
    public String getShopName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>mini_main.b2c_user_login_record.sys_id</code>. 主账户ID
     */
    public void setSysId(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_main.b2c_user_login_record.sys_id</code>. 主账户ID
     */
    public Integer getSysId() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>mini_main.b2c_user_login_record.user_id</code>.
     */
    public void setUserId(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_main.b2c_user_login_record.user_id</code>.
     */
    public Integer getUserId() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>mini_main.b2c_user_login_record.user_name</code>. 登陆用户名
     */
    public void setUserName(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_main.b2c_user_login_record.user_name</code>. 登陆用户名
     */
    public String getUserName() {
        return (String) get(5);
    }

    /**
     * Setter for <code>mini_main.b2c_user_login_record.add_time</code>. 每日登陆时间
     */
    public void setAddTime(Timestamp value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_main.b2c_user_login_record.add_time</code>. 每日登陆时间
     */
    public Timestamp getAddTime() {
        return (Timestamp) get(6);
    }

    /**
     * Setter for <code>mini_main.b2c_user_login_record.user_ip</code>. 用户登录ip
     */
    public void setUserIp(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_main.b2c_user_login_record.user_ip</code>. 用户登录ip
     */
    public String getUserIp() {
        return (String) get(7);
    }

    /**
     * Setter for <code>mini_main.b2c_user_login_record.count</code>. 每日登陆次数
     */
    public void setCount(Short value) {
        set(8, value);
    }

    /**
     * Getter for <code>mini_main.b2c_user_login_record.count</code>. 每日登陆次数
     */
    public Short getCount() {
        return (Short) get(8);
    }

    /**
     * Setter for <code>mini_main.b2c_user_login_record.account_type</code>. 登录日志账户类型：0店铺登录日志，1系统账号登录日志
     */
    public void setAccountType(Byte value) {
        set(9, value);
    }

    /**
     * Getter for <code>mini_main.b2c_user_login_record.account_type</code>. 登录日志账户类型：0店铺登录日志，1系统账号登录日志
     */
    public Byte getAccountType() {
        return (Byte) get(9);
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
    // Record10 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<Integer, Integer, String, Integer, Integer, String, Timestamp, String, Short, Byte> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<Integer, Integer, String, Integer, Integer, String, Timestamp, String, Short, Byte> valuesRow() {
        return (Row10) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return UserLoginRecord.USER_LOGIN_RECORD.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return UserLoginRecord.USER_LOGIN_RECORD.SHOP_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return UserLoginRecord.USER_LOGIN_RECORD.SHOP_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return UserLoginRecord.USER_LOGIN_RECORD.SYS_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field5() {
        return UserLoginRecord.USER_LOGIN_RECORD.USER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return UserLoginRecord.USER_LOGIN_RECORD.USER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field7() {
        return UserLoginRecord.USER_LOGIN_RECORD.ADD_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return UserLoginRecord.USER_LOGIN_RECORD.USER_IP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field9() {
        return UserLoginRecord.USER_LOGIN_RECORD.COUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field10() {
        return UserLoginRecord.USER_LOGIN_RECORD.ACCOUNT_TYPE;
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
    public String component3() {
        return getShopName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getSysId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component5() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getUserName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component7() {
        return getAddTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getUserIp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short component9() {
        return getCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component10() {
        return getAccountType();
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
    public String value3() {
        return getShopName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getSysId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value5() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getUserName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value7() {
        return getAddTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getUserIp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value9() {
        return getCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value10() {
        return getAccountType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserLoginRecordRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserLoginRecordRecord value2(Integer value) {
        setShopId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserLoginRecordRecord value3(String value) {
        setShopName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserLoginRecordRecord value4(Integer value) {
        setSysId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserLoginRecordRecord value5(Integer value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserLoginRecordRecord value6(String value) {
        setUserName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserLoginRecordRecord value7(Timestamp value) {
        setAddTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserLoginRecordRecord value8(String value) {
        setUserIp(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserLoginRecordRecord value9(Short value) {
        setCount(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserLoginRecordRecord value10(Byte value) {
        setAccountType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserLoginRecordRecord values(Integer value1, Integer value2, String value3, Integer value4, Integer value5, String value6, Timestamp value7, String value8, Short value9, Byte value10) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UserLoginRecordRecord
     */
    public UserLoginRecordRecord() {
        super(UserLoginRecord.USER_LOGIN_RECORD);
    }

    /**
     * Create a detached, initialised UserLoginRecordRecord
     */
    public UserLoginRecordRecord(Integer id, Integer shopId, String shopName, Integer sysId, Integer userId, String userName, Timestamp addTime, String userIp, Short count, Byte accountType) {
        super(UserLoginRecord.USER_LOGIN_RECORD);

        set(0, id);
        set(1, shopId);
        set(2, shopName);
        set(3, sysId);
        set(4, userId);
        set(5, userName);
        set(6, addTime);
        set(7, userIp);
        set(8, count);
        set(9, accountType);
    }
}
