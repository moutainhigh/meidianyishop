/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.GroupIntegrationList;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record16;
import org.jooq.Row16;
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
public class GroupIntegrationListRecord extends UpdatableRecordImpl<GroupIntegrationListRecord> implements Record16<Integer, Integer, Integer, Integer, Byte, Byte, Timestamp, Timestamp, Integer, Short, Integer, Byte, Byte, Integer, Timestamp, Timestamp> {

    private static final long serialVersionUID = 284128688;

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.inte_activity_id</code>. 瓜分积分活动定义id
     */
    public void setInteActivityId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.inte_activity_id</code>. 瓜分积分活动定义id
     */
    public Integer getInteActivityId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.group_id</code>. 拼团ID
     */
    public void setGroupId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.group_id</code>. 拼团ID
     */
    public Integer getGroupId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.user_id</code>.
     */
    public void setUserId(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.user_id</code>.
     */
    public Integer getUserId() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.is_grouper</code>. 是否为团长 1：是 0：否
     */
    public void setIsGrouper(Byte value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.is_grouper</code>. 是否为团长 1：是 0：否
     */
    public Byte getIsGrouper() {
        return (Byte) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.status</code>. 0: 拼团中 1:拼团成功 2:拼团失败
     */
    public void setStatus(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.status</code>. 0: 拼团中 1:拼团成功 2:拼团失败
     */
    public Byte getStatus() {
        return (Byte) get(5);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.start_time</code>. 参团时间
     */
    public void setStartTime(Timestamp value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.start_time</code>. 参团时间
     */
    public Timestamp getStartTime() {
        return (Timestamp) get(6);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.end_time</code>. 成团时间
     */
    public void setEndTime(Timestamp value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.end_time</code>. 成团时间
     */
    public Timestamp getEndTime() {
        return (Timestamp) get(7);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.integration</code>. 瓜分到的积分
     */
    public void setIntegration(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.integration</code>. 瓜分到的积分
     */
    public Integer getIntegration() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.invite_num</code>. 邀请人的数量
     */
    public void setInviteNum(Short value) {
        set(9, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.invite_num</code>. 邀请人的数量
     */
    public Short getInviteNum() {
        return (Short) get(9);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.invite_user</code>. 邀请人（被谁邀请）
     */
    public void setInviteUser(Integer value) {
        set(10, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.invite_user</code>. 邀请人（被谁邀请）
     */
    public Integer getInviteUser() {
        return (Integer) get(10);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.is_new</code>. 是否是新用户：0：不是，1：是
     */
    public void setIsNew(Byte value) {
        set(11, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.is_new</code>. 是否是新用户：0：不是，1：是
     */
    public Byte getIsNew() {
        return (Byte) get(11);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.is_look</code>. 是否看过开奖结果
     */
    public void setIsLook(Byte value) {
        set(12, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.is_look</code>. 是否看过开奖结果
     */
    public Byte getIsLook() {
        return (Byte) get(12);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.can_integration</code>. 该团可瓜分积分池
     */
    public void setCanIntegration(Integer value) {
        set(13, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.can_integration</code>. 该团可瓜分积分池
     */
    public Integer getCanIntegration() {
        return (Integer) get(13);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.create_time</code>.
     */
    public void setCreateTime(Timestamp value) {
        set(14, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.create_time</code>.
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(14);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_integration_list.update_time</code>. 最后修改时间
     */
    public void setUpdateTime(Timestamp value) {
        set(15, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_integration_list.update_time</code>. 最后修改时间
     */
    public Timestamp getUpdateTime() {
        return (Timestamp) get(15);
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
    // Record16 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row16<Integer, Integer, Integer, Integer, Byte, Byte, Timestamp, Timestamp, Integer, Short, Integer, Byte, Byte, Integer, Timestamp, Timestamp> fieldsRow() {
        return (Row16) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row16<Integer, Integer, Integer, Integer, Byte, Byte, Timestamp, Timestamp, Integer, Short, Integer, Byte, Byte, Integer, Timestamp, Timestamp> valuesRow() {
        return (Row16) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.INTE_ACTIVITY_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.GROUP_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.USER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field5() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.IS_GROUPER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field6() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field7() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.START_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field8() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.END_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field9() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.INTEGRATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field10() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.INVITE_NUM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field11() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.INVITE_USER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field12() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.IS_NEW;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field13() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.IS_LOOK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field14() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.CAN_INTEGRATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field15() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field16() {
        return GroupIntegrationList.GROUP_INTEGRATION_LIST.UPDATE_TIME;
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
        return getInteActivityId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component3() {
        return getGroupId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component5() {
        return getIsGrouper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component6() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component7() {
        return getStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component8() {
        return getEndTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component9() {
        return getIntegration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short component10() {
        return getInviteNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component11() {
        return getInviteUser();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component12() {
        return getIsNew();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component13() {
        return getIsLook();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component14() {
        return getCanIntegration();
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
    public Integer value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getInteActivityId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getGroupId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value5() {
        return getIsGrouper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value6() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value7() {
        return getStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value8() {
        return getEndTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value9() {
        return getIntegration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value10() {
        return getInviteNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value11() {
        return getInviteUser();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value12() {
        return getIsNew();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value13() {
        return getIsLook();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value14() {
        return getCanIntegration();
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
    public GroupIntegrationListRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord value2(Integer value) {
        setInteActivityId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord value3(Integer value) {
        setGroupId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord value4(Integer value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord value5(Byte value) {
        setIsGrouper(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord value6(Byte value) {
        setStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord value7(Timestamp value) {
        setStartTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord value8(Timestamp value) {
        setEndTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord value9(Integer value) {
        setIntegration(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord value10(Short value) {
        setInviteNum(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord value11(Integer value) {
        setInviteUser(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord value12(Byte value) {
        setIsNew(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord value13(Byte value) {
        setIsLook(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord value14(Integer value) {
        setCanIntegration(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord value15(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord value16(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupIntegrationListRecord values(Integer value1, Integer value2, Integer value3, Integer value4, Byte value5, Byte value6, Timestamp value7, Timestamp value8, Integer value9, Short value10, Integer value11, Byte value12, Byte value13, Integer value14, Timestamp value15, Timestamp value16) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached GroupIntegrationListRecord
     */
    public GroupIntegrationListRecord() {
        super(GroupIntegrationList.GROUP_INTEGRATION_LIST);
    }

    /**
     * Create a detached, initialised GroupIntegrationListRecord
     */
    public GroupIntegrationListRecord(Integer id, Integer inteActivityId, Integer groupId, Integer userId, Byte isGrouper, Byte status, Timestamp startTime, Timestamp endTime, Integer integration, Short inviteNum, Integer inviteUser, Byte isNew, Byte isLook, Integer canIntegration, Timestamp createTime, Timestamp updateTime) {
        super(GroupIntegrationList.GROUP_INTEGRATION_LIST);

        set(0, id);
        set(1, inteActivityId);
        set(2, groupId);
        set(3, userId);
        set(4, isGrouper);
        set(5, status);
        set(6, startTime);
        set(7, endTime);
        set(8, integration);
        set(9, inviteNum);
        set(10, inviteUser);
        set(11, isNew);
        set(12, isLook);
        set(13, canIntegration);
        set(14, createTime);
        set(15, updateTime);
    }
}