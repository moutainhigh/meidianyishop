/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.MpVisitPage;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Row11;
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
public class MpVisitPageRecord extends UpdatableRecordImpl<MpVisitPageRecord> implements Record11<String, String, Integer, Integer, Double, Integer, Integer, Integer, Integer, Timestamp, Integer> {

    private static final long serialVersionUID = 1895509005;

    /**
     * Setter for <code>jmini_shop_666666.b2c_mp_visit_page.ref_date</code>. 时间，如："20170313"
     */
    public void setRefDate(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>jmini_shop_666666.b2c_mp_visit_page.ref_date</code>. 时间，如："20170313"
     */
    public String getRefDate() {
        return (String) get(0);
    }

    /**
     * Setter for <code>jmini_shop_666666.b2c_mp_visit_page.page_path</code>.
     */
    public void setPagePath(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>jmini_shop_666666.b2c_mp_visit_page.page_path</code>.
     */
    public String getPagePath() {
        return (String) get(1);
    }

    /**
     * Setter for <code>jmini_shop_666666.b2c_mp_visit_page.page_visit_pv</code>. 访问次数
     */
    public void setPageVisitPv(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>jmini_shop_666666.b2c_mp_visit_page.page_visit_pv</code>. 访问次数
     */
    public Integer getPageVisitPv() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>jmini_shop_666666.b2c_mp_visit_page.page_visit_uv</code>. 访问人数
     */
    public void setPageVisitUv(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>jmini_shop_666666.b2c_mp_visit_page.page_visit_uv</code>. 访问人数
     */
    public Integer getPageVisitUv() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>jmini_shop_666666.b2c_mp_visit_page.page_staytime_pv</code>. 人均停留时长 (浮点型，单位：秒)
     */
    public void setPageStaytimePv(Double value) {
        set(4, value);
    }

    /**
     * Getter for <code>jmini_shop_666666.b2c_mp_visit_page.page_staytime_pv</code>. 人均停留时长 (浮点型，单位：秒)
     */
    public Double getPageStaytimePv() {
        return (Double) get(4);
    }

    /**
     * Setter for <code>jmini_shop_666666.b2c_mp_visit_page.entrypage_pv</code>. 进入页次数
     */
    public void setEntrypagePv(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>jmini_shop_666666.b2c_mp_visit_page.entrypage_pv</code>. 进入页次数
     */
    public Integer getEntrypagePv() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>jmini_shop_666666.b2c_mp_visit_page.exitpage_pv</code>. 退出页次数
     */
    public void setExitpagePv(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>jmini_shop_666666.b2c_mp_visit_page.exitpage_pv</code>. 退出页次数
     */
    public Integer getExitpagePv() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>jmini_shop_666666.b2c_mp_visit_page.page_share_pv</code>. 转发次数
     */
    public void setPageSharePv(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>jmini_shop_666666.b2c_mp_visit_page.page_share_pv</code>. 转发次数
     */
    public Integer getPageSharePv() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>jmini_shop_666666.b2c_mp_visit_page.page_share_uv</code>. 转发人数
     */
    public void setPageShareUv(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>jmini_shop_666666.b2c_mp_visit_page.page_share_uv</code>. 转发人数
     */
    public Integer getPageShareUv() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>jmini_shop_666666.b2c_mp_visit_page.create_time</code>. 添加时间
     */
    public void setCreateTime(Timestamp value) {
        set(9, value);
    }

    /**
     * Getter for <code>jmini_shop_666666.b2c_mp_visit_page.create_time</code>. 添加时间
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(9);
    }

    /**
     * Setter for <code>jmini_shop_666666.b2c_mp_visit_page.id</code>.
     */
    public void setId(Integer value) {
        set(10, value);
    }

    /**
     * Getter for <code>jmini_shop_666666.b2c_mp_visit_page.id</code>.
     */
    public Integer getId() {
        return (Integer) get(10);
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
    // Record11 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row11<String, String, Integer, Integer, Double, Integer, Integer, Integer, Integer, Timestamp, Integer> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row11<String, String, Integer, Integer, Double, Integer, Integer, Integer, Integer, Timestamp, Integer> valuesRow() {
        return (Row11) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return MpVisitPage.MP_VISIT_PAGE.REF_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return MpVisitPage.MP_VISIT_PAGE.PAGE_PATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return MpVisitPage.MP_VISIT_PAGE.PAGE_VISIT_PV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return MpVisitPage.MP_VISIT_PAGE.PAGE_VISIT_UV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Double> field5() {
        return MpVisitPage.MP_VISIT_PAGE.PAGE_STAYTIME_PV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field6() {
        return MpVisitPage.MP_VISIT_PAGE.ENTRYPAGE_PV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field7() {
        return MpVisitPage.MP_VISIT_PAGE.EXITPAGE_PV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field8() {
        return MpVisitPage.MP_VISIT_PAGE.PAGE_SHARE_PV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field9() {
        return MpVisitPage.MP_VISIT_PAGE.PAGE_SHARE_UV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field10() {
        return MpVisitPage.MP_VISIT_PAGE.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field11() {
        return MpVisitPage.MP_VISIT_PAGE.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getRefDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getPagePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component3() {
        return getPageVisitPv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getPageVisitUv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double component5() {
        return getPageStaytimePv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component6() {
        return getEntrypagePv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component7() {
        return getExitpagePv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component8() {
        return getPageSharePv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component9() {
        return getPageShareUv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component10() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component11() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getRefDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getPagePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getPageVisitPv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getPageVisitUv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double value5() {
        return getPageStaytimePv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value6() {
        return getEntrypagePv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value7() {
        return getExitpagePv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value8() {
        return getPageSharePv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value9() {
        return getPageShareUv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value10() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value11() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpVisitPageRecord value1(String value) {
        setRefDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpVisitPageRecord value2(String value) {
        setPagePath(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpVisitPageRecord value3(Integer value) {
        setPageVisitPv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpVisitPageRecord value4(Integer value) {
        setPageVisitUv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpVisitPageRecord value5(Double value) {
        setPageStaytimePv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpVisitPageRecord value6(Integer value) {
        setEntrypagePv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpVisitPageRecord value7(Integer value) {
        setExitpagePv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpVisitPageRecord value8(Integer value) {
        setPageSharePv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpVisitPageRecord value9(Integer value) {
        setPageShareUv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpVisitPageRecord value10(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpVisitPageRecord value11(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MpVisitPageRecord values(String value1, String value2, Integer value3, Integer value4, Double value5, Integer value6, Integer value7, Integer value8, Integer value9, Timestamp value10, Integer value11) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MpVisitPageRecord
     */
    public MpVisitPageRecord() {
        super(MpVisitPage.MP_VISIT_PAGE);
    }

    /**
     * Create a detached, initialised MpVisitPageRecord
     */
    public MpVisitPageRecord(String refDate, String pagePath, Integer pageVisitPv, Integer pageVisitUv, Double pageStaytimePv, Integer entrypagePv, Integer exitpagePv, Integer pageSharePv, Integer pageShareUv, Timestamp createTime, Integer id) {
        super(MpVisitPage.MP_VISIT_PAGE);

        set(0, refDate);
        set(1, pagePath);
        set(2, pageVisitPv);
        set(3, pageVisitUv);
        set(4, pageStaytimePv);
        set(5, entrypagePv);
        set(6, exitpagePv);
        set(7, pageSharePv);
        set(8, pageShareUv);
        set(9, createTime);
        set(10, id);
    }
}
