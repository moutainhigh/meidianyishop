/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.FriendPromoteTimes;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
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
public class FriendPromoteTimesRecord extends UpdatableRecordImpl<FriendPromoteTimesRecord> implements Record8<Integer, Integer, Integer, Integer, Integer, Byte, Timestamp, Timestamp> {

    private static final long serialVersionUID = -677269487;

    /**
     * Setter for <code>mini_shop_471752.b2c_friend_promote_times.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_friend_promote_times.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_friend_promote_times.launch_id</code>. 助力活动发起id
     */
    public void setLaunchId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_friend_promote_times.launch_id</code>. 助力活动发起id
     */
    public Integer getLaunchId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_friend_promote_times.user_id</code>. 助力会员id
     */
    public void setUserId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_friend_promote_times.user_id</code>. 助力会员id
     */
    public Integer getUserId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_friend_promote_times.share_times</code>. 分享的次数
     */
    public void setShareTimes(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_friend_promote_times.share_times</code>. 分享的次数
     */
    public Integer getShareTimes() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_friend_promote_times.own_promote_times</code>. 总的所有助力次数
     */
    public void setOwnPromoteTimes(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_friend_promote_times.own_promote_times</code>. 总的所有助力次数
     */
    public Integer getOwnPromoteTimes() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_friend_promote_times.is_auth</code>. 是否有授权增加次数：0没有，1有
     */
    public void setIsAuth(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_friend_promote_times.is_auth</code>. 是否有授权增加次数：0没有，1有
     */
    public Byte getIsAuth() {
        return (Byte) get(5);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_friend_promote_times.create_time</code>. 助力时间
     */
    public void setCreateTime(Timestamp value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_friend_promote_times.create_time</code>. 助力时间
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(6);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_friend_promote_times.update_time</code>. 更新时间
     */
    public void setUpdateTime(Timestamp value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_friend_promote_times.update_time</code>. 更新时间
     */
    public Timestamp getUpdateTime() {
        return (Timestamp) get(7);
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
    // Record8 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<Integer, Integer, Integer, Integer, Integer, Byte, Timestamp, Timestamp> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<Integer, Integer, Integer, Integer, Integer, Byte, Timestamp, Timestamp> valuesRow() {
        return (Row8) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return FriendPromoteTimes.FRIEND_PROMOTE_TIMES.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return FriendPromoteTimes.FRIEND_PROMOTE_TIMES.LAUNCH_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return FriendPromoteTimes.FRIEND_PROMOTE_TIMES.USER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return FriendPromoteTimes.FRIEND_PROMOTE_TIMES.SHARE_TIMES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field5() {
        return FriendPromoteTimes.FRIEND_PROMOTE_TIMES.OWN_PROMOTE_TIMES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field6() {
        return FriendPromoteTimes.FRIEND_PROMOTE_TIMES.IS_AUTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field7() {
        return FriendPromoteTimes.FRIEND_PROMOTE_TIMES.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field8() {
        return FriendPromoteTimes.FRIEND_PROMOTE_TIMES.UPDATE_TIME;
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
        return getLaunchId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component3() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getShareTimes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component5() {
        return getOwnPromoteTimes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component6() {
        return getIsAuth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component7() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component8() {
        return getUpdateTime();
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
        return getLaunchId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getShareTimes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value5() {
        return getOwnPromoteTimes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value6() {
        return getIsAuth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value7() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value8() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FriendPromoteTimesRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FriendPromoteTimesRecord value2(Integer value) {
        setLaunchId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FriendPromoteTimesRecord value3(Integer value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FriendPromoteTimesRecord value4(Integer value) {
        setShareTimes(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FriendPromoteTimesRecord value5(Integer value) {
        setOwnPromoteTimes(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FriendPromoteTimesRecord value6(Byte value) {
        setIsAuth(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FriendPromoteTimesRecord value7(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FriendPromoteTimesRecord value8(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FriendPromoteTimesRecord values(Integer value1, Integer value2, Integer value3, Integer value4, Integer value5, Byte value6, Timestamp value7, Timestamp value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached FriendPromoteTimesRecord
     */
    public FriendPromoteTimesRecord() {
        super(FriendPromoteTimes.FRIEND_PROMOTE_TIMES);
    }

    /**
     * Create a detached, initialised FriendPromoteTimesRecord
     */
    public FriendPromoteTimesRecord(Integer id, Integer launchId, Integer userId, Integer shareTimes, Integer ownPromoteTimes, Byte isAuth, Timestamp createTime, Timestamp updateTime) {
        super(FriendPromoteTimes.FRIEND_PROMOTE_TIMES);

        set(0, id);
        set(1, launchId);
        set(2, userId);
        set(3, shareTimes);
        set(4, ownPromoteTimes);
        set(5, isAuth);
        set(6, createTime);
        set(7, updateTime);
    }
}