/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.GroupDraw;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record19;
import org.jooq.Row19;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * 拼团抽奖配置页
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GroupDrawRecord extends UpdatableRecordImpl<GroupDrawRecord> implements Record19<Integer, String, Timestamp, Timestamp, String, Short, BigDecimal, Short, Short, Short, Short, Byte, Byte, Timestamp, Timestamp, Byte, Timestamp, String, String> {

    private static final long serialVersionUID = -900452769;

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.name</code>. 活动名称
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.name</code>. 活动名称
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.start_time</code>. 开始时间
     */
    public void setStartTime(Timestamp value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.start_time</code>. 开始时间
     */
    public Timestamp getStartTime() {
        return (Timestamp) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.end_time</code>. 结束时间
     */
    public void setEndTime(Timestamp value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.end_time</code>. 结束时间
     */
    public Timestamp getEndTime() {
        return (Timestamp) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.goods_id</code>. 参与抽奖的商品id
     */
    public void setGoodsId(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.goods_id</code>. 参与抽奖的商品id
     */
    public String getGoodsId() {
        return (String) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.min_join_num</code>. 开奖最小参与人数
     */
    public void setMinJoinNum(Short value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.min_join_num</code>. 开奖最小参与人数
     */
    public Short getMinJoinNum() {
        return (Short) get(5);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.pay_money</code>. 下单支付金额
     */
    public void setPayMoney(BigDecimal value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.pay_money</code>. 下单支付金额
     */
    public BigDecimal getPayMoney() {
        return (BigDecimal) get(6);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.join_limit</code>. 参团限制
     */
    public void setJoinLimit(Short value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.join_limit</code>. 参团限制
     */
    public Short getJoinLimit() {
        return (Short) get(7);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.open_limit</code>. 开团限制
     */
    public void setOpenLimit(Short value) {
        set(8, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.open_limit</code>. 开团限制
     */
    public Short getOpenLimit() {
        return (Short) get(8);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.limit_amount</code>. 最小成团人数
     */
    public void setLimitAmount(Short value) {
        set(9, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.limit_amount</code>. 最小成团人数
     */
    public Short getLimitAmount() {
        return (Short) get(9);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.to_num_show</code>. 参与用户达到多少前端展示
     */
    public void setToNumShow(Short value) {
        set(10, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.to_num_show</code>. 参与用户达到多少前端展示
     */
    public Short getToNumShow() {
        return (Short) get(10);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.status</code>. 1：启用 0：禁用
     */
    public void setStatus(Byte value) {
        set(11, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.status</code>. 1：启用 0：禁用
     */
    public Byte getStatus() {
        return (Byte) get(11);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.is_draw</code>. 是否已开奖
     */
    public void setIsDraw(Byte value) {
        set(12, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.is_draw</code>. 是否已开奖
     */
    public Byte getIsDraw() {
        return (Byte) get(12);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.create_time</code>.
     */
    public void setCreateTime(Timestamp value) {
        set(13, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.create_time</code>.
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(13);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.update_time</code>. 最后修改时间
     */
    public void setUpdateTime(Timestamp value) {
        set(14, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.update_time</code>. 最后修改时间
     */
    public Timestamp getUpdateTime() {
        return (Timestamp) get(14);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.del_flag</code>.
     */
    public void setDelFlag(Byte value) {
        set(15, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.del_flag</code>.
     */
    public Byte getDelFlag() {
        return (Byte) get(15);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.del_time</code>. 删除时间
     */
    public void setDelTime(Timestamp value) {
        set(16, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.del_time</code>. 删除时间
     */
    public Timestamp getDelTime() {
        return (Timestamp) get(16);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.reward_coupon_id</code>. 拼团失败发放优惠券
     */
    public void setRewardCouponId(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.reward_coupon_id</code>. 拼团失败发放优惠券
     */
    public String getRewardCouponId() {
        return (String) get(17);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_group_draw.activity_copywriting</code>. 活动说明
     */
    public void setActivityCopywriting(String value) {
        set(18, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_group_draw.activity_copywriting</code>. 活动说明
     */
    public String getActivityCopywriting() {
        return (String) get(18);
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
    public Row19<Integer, String, Timestamp, Timestamp, String, Short, BigDecimal, Short, Short, Short, Short, Byte, Byte, Timestamp, Timestamp, Byte, Timestamp, String, String> fieldsRow() {
        return (Row19) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row19<Integer, String, Timestamp, Timestamp, String, Short, BigDecimal, Short, Short, Short, Short, Byte, Byte, Timestamp, Timestamp, Byte, Timestamp, String, String> valuesRow() {
        return (Row19) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return GroupDraw.GROUP_DRAW.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return GroupDraw.GROUP_DRAW.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field3() {
        return GroupDraw.GROUP_DRAW.START_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field4() {
        return GroupDraw.GROUP_DRAW.END_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return GroupDraw.GROUP_DRAW.GOODS_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field6() {
        return GroupDraw.GROUP_DRAW.MIN_JOIN_NUM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field7() {
        return GroupDraw.GROUP_DRAW.PAY_MONEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field8() {
        return GroupDraw.GROUP_DRAW.JOIN_LIMIT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field9() {
        return GroupDraw.GROUP_DRAW.OPEN_LIMIT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field10() {
        return GroupDraw.GROUP_DRAW.LIMIT_AMOUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field11() {
        return GroupDraw.GROUP_DRAW.TO_NUM_SHOW;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field12() {
        return GroupDraw.GROUP_DRAW.STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field13() {
        return GroupDraw.GROUP_DRAW.IS_DRAW;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field14() {
        return GroupDraw.GROUP_DRAW.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field15() {
        return GroupDraw.GROUP_DRAW.UPDATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field16() {
        return GroupDraw.GROUP_DRAW.DEL_FLAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field17() {
        return GroupDraw.GROUP_DRAW.DEL_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field18() {
        return GroupDraw.GROUP_DRAW.REWARD_COUPON_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field19() {
        return GroupDraw.GROUP_DRAW.ACTIVITY_COPYWRITING;
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
    public String component2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component3() {
        return getStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component4() {
        return getEndTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getGoodsId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short component6() {
        return getMinJoinNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component7() {
        return getPayMoney();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short component8() {
        return getJoinLimit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short component9() {
        return getOpenLimit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short component10() {
        return getLimitAmount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short component11() {
        return getToNumShow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component12() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component13() {
        return getIsDraw();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component14() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component15() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component16() {
        return getDelFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component17() {
        return getDelTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component18() {
        return getRewardCouponId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component19() {
        return getActivityCopywriting();
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
    public String value2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value3() {
        return getStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value4() {
        return getEndTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getGoodsId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value6() {
        return getMinJoinNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value7() {
        return getPayMoney();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value8() {
        return getJoinLimit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value9() {
        return getOpenLimit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value10() {
        return getLimitAmount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value11() {
        return getToNumShow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value12() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value13() {
        return getIsDraw();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value14() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value15() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value16() {
        return getDelFlag();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value17() {
        return getDelTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value18() {
        return getRewardCouponId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value19() {
        return getActivityCopywriting();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value2(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value3(Timestamp value) {
        setStartTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value4(Timestamp value) {
        setEndTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value5(String value) {
        setGoodsId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value6(Short value) {
        setMinJoinNum(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value7(BigDecimal value) {
        setPayMoney(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value8(Short value) {
        setJoinLimit(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value9(Short value) {
        setOpenLimit(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value10(Short value) {
        setLimitAmount(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value11(Short value) {
        setToNumShow(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value12(Byte value) {
        setStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value13(Byte value) {
        setIsDraw(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value14(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value15(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value16(Byte value) {
        setDelFlag(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value17(Timestamp value) {
        setDelTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value18(String value) {
        setRewardCouponId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord value19(String value) {
        setActivityCopywriting(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupDrawRecord values(Integer value1, String value2, Timestamp value3, Timestamp value4, String value5, Short value6, BigDecimal value7, Short value8, Short value9, Short value10, Short value11, Byte value12, Byte value13, Timestamp value14, Timestamp value15, Byte value16, Timestamp value17, String value18, String value19) {
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
     * Create a detached GroupDrawRecord
     */
    public GroupDrawRecord() {
        super(GroupDraw.GROUP_DRAW);
    }

    /**
     * Create a detached, initialised GroupDrawRecord
     */
    public GroupDrawRecord(Integer id, String name, Timestamp startTime, Timestamp endTime, String goodsId, Short minJoinNum, BigDecimal payMoney, Short joinLimit, Short openLimit, Short limitAmount, Short toNumShow, Byte status, Byte isDraw, Timestamp createTime, Timestamp updateTime, Byte delFlag, Timestamp delTime, String rewardCouponId, String activityCopywriting) {
        super(GroupDraw.GROUP_DRAW);

        set(0, id);
        set(1, name);
        set(2, startTime);
        set(3, endTime);
        set(4, goodsId);
        set(5, minJoinNum);
        set(6, payMoney);
        set(7, joinLimit);
        set(8, openLimit);
        set(9, limitAmount);
        set(10, toNumShow);
        set(11, status);
        set(12, isDraw);
        set(13, createTime);
        set(14, updateTime);
        set(15, delFlag);
        set(16, delTime);
        set(17, rewardCouponId);
        set(18, activityCopywriting);
    }
}
