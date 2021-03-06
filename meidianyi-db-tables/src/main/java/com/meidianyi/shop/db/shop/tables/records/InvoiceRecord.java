/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables.records;


import com.meidianyi.shop.db.shop.tables.Invoice;

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
public class InvoiceRecord extends UpdatableRecordImpl<InvoiceRecord> implements Record11<Integer, Integer, Byte, String, String, String, String, String, String, Timestamp, Timestamp> {

    private static final long serialVersionUID = -559663677;

    /**
     * Setter for <code>mini_shop_471752.b2c_invoice.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_invoice.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_invoice.user_id</code>. 用户id
     */
    public void setUserId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_invoice.user_id</code>. 用户id
     */
    public Integer getUserId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_invoice.type</code>. 发票类型
     */
    public void setType(Byte value) {
        set(2, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_invoice.type</code>. 发票类型
     */
    public Byte getType() {
        return (Byte) get(2);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_invoice.title</code>. 公司名称
     */
    public void setTitle(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_invoice.title</code>. 公司名称
     */
    public String getTitle() {
        return (String) get(3);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_invoice.telephone</code>. 公司电话
     */
    public void setTelephone(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_invoice.telephone</code>. 公司电话
     */
    public String getTelephone() {
        return (String) get(4);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_invoice.taxnumber</code>. 税号
     */
    public void setTaxnumber(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_invoice.taxnumber</code>. 税号
     */
    public String getTaxnumber() {
        return (String) get(5);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_invoice.companyaddress</code>. 公司地址
     */
    public void setCompanyaddress(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_invoice.companyaddress</code>. 公司地址
     */
    public String getCompanyaddress() {
        return (String) get(6);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_invoice.bankname</code>. 银行名称
     */
    public void setBankname(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_invoice.bankname</code>. 银行名称
     */
    public String getBankname() {
        return (String) get(7);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_invoice.bankaccount</code>. 银行账号
     */
    public void setBankaccount(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_invoice.bankaccount</code>. 银行账号
     */
    public String getBankaccount() {
        return (String) get(8);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_invoice.create_time</code>.
     */
    public void setCreateTime(Timestamp value) {
        set(9, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_invoice.create_time</code>.
     */
    public Timestamp getCreateTime() {
        return (Timestamp) get(9);
    }

    /**
     * Setter for <code>mini_shop_471752.b2c_invoice.update_time</code>. 最后修改时间
     */
    public void setUpdateTime(Timestamp value) {
        set(10, value);
    }

    /**
     * Getter for <code>mini_shop_471752.b2c_invoice.update_time</code>. 最后修改时间
     */
    public Timestamp getUpdateTime() {
        return (Timestamp) get(10);
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
    public Row11<Integer, Integer, Byte, String, String, String, String, String, String, Timestamp, Timestamp> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row11<Integer, Integer, Byte, String, String, String, String, String, String, Timestamp, Timestamp> valuesRow() {
        return (Row11) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Invoice.INVOICE.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return Invoice.INVOICE.USER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field3() {
        return Invoice.INVOICE.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Invoice.INVOICE.TITLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Invoice.INVOICE.TELEPHONE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Invoice.INVOICE.TAXNUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Invoice.INVOICE.COMPANYADDRESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Invoice.INVOICE.BANKNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Invoice.INVOICE.BANKACCOUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field10() {
        return Invoice.INVOICE.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field11() {
        return Invoice.INVOICE.UPDATE_TIME;
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
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component3() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getTitle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getTelephone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getTaxnumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component7() {
        return getCompanyaddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getBankname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component9() {
        return getBankaccount();
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
    public Timestamp component11() {
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
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value3() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getTitle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getTelephone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getTaxnumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getCompanyaddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getBankname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getBankaccount();
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
    public Timestamp value11() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvoiceRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvoiceRecord value2(Integer value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvoiceRecord value3(Byte value) {
        setType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvoiceRecord value4(String value) {
        setTitle(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvoiceRecord value5(String value) {
        setTelephone(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvoiceRecord value6(String value) {
        setTaxnumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvoiceRecord value7(String value) {
        setCompanyaddress(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvoiceRecord value8(String value) {
        setBankname(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvoiceRecord value9(String value) {
        setBankaccount(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvoiceRecord value10(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvoiceRecord value11(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvoiceRecord values(Integer value1, Integer value2, Byte value3, String value4, String value5, String value6, String value7, String value8, String value9, Timestamp value10, Timestamp value11) {
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
     * Create a detached InvoiceRecord
     */
    public InvoiceRecord() {
        super(Invoice.INVOICE);
    }

    /**
     * Create a detached, initialised InvoiceRecord
     */
    public InvoiceRecord(Integer id, Integer userId, Byte type, String title, String telephone, String taxnumber, String companyaddress, String bankname, String bankaccount, Timestamp createTime, Timestamp updateTime) {
        super(Invoice.INVOICE);

        set(0, id);
        set(1, userId);
        set(2, type);
        set(3, title);
        set(4, telephone);
        set(5, taxnumber);
        set(6, companyaddress);
        set(7, bankname);
        set(8, bankaccount);
        set(9, createTime);
        set(10, updateTime);
    }
}
