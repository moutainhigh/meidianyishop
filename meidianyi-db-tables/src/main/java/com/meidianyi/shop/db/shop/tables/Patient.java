/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.PatientRecord;

import java.sql.Date;
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
 * 患者信息
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Patient extends TableImpl<PatientRecord> {

    private static final long serialVersionUID = 464881554;

    /**
     * The reference instance of <code>mini_shop_471752.b2c_patient</code>
     */
    public static final Patient PATIENT = new Patient();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PatientRecord> getRecordType() {
        return PatientRecord.class;
    }

    /**
     * The column <code>mini_shop_471752.b2c_patient.id</code>.
     */
    public final TableField<PatientRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_patient.name</code>. 患者名称
     */
    public final TableField<PatientRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "患者名称");

    /**
     * The column <code>mini_shop_471752.b2c_patient.mobile</code>. 手机号码
     */
    public final TableField<PatientRecord, String> MOBILE = createField("mobile", org.jooq.impl.SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "手机号码");

    /**
     * The column <code>mini_shop_471752.b2c_patient.patient_code</code>. 医院内部患者唯一编码
     */
    public final TableField<PatientRecord, String> PATIENT_CODE = createField("patient_code", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "医院内部患者唯一编码");

    /**
     * The column <code>mini_shop_471752.b2c_patient.identity_code</code>. 证件号码
     */
    public final TableField<PatientRecord, String> IDENTITY_CODE = createField("identity_code", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "证件号码");

    /**
     * The column <code>mini_shop_471752.b2c_patient.identity_type</code>. 证件类型: 1：身份证 2：军人证 3：护照 4：社保卡
     */
    public final TableField<PatientRecord, Byte> IDENTITY_TYPE = createField("identity_type", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("1", org.jooq.impl.SQLDataType.TINYINT)), this, "证件类型: 1：身份证 2：军人证 3：护照 4：社保卡");

    /**
     * The column <code>mini_shop_471752.b2c_patient.treatment_code</code>. 就诊卡号
     */
    public final TableField<PatientRecord, String> TREATMENT_CODE = createField("treatment_code", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "就诊卡号");

    /**
     * The column <code>mini_shop_471752.b2c_patient.insurance_card_code</code>. 医保卡号
     */
    public final TableField<PatientRecord, String> INSURANCE_CARD_CODE = createField("insurance_card_code", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false).defaultValue(DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "医保卡号");

    /**
     * The column <code>mini_shop_471752.b2c_patient.sex</code>. 性别 0：未知 1：男 2：女
     */
    public final TableField<PatientRecord, Byte> SEX = createField("sex", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("1", org.jooq.impl.SQLDataType.TINYINT)), this, "性别 0：未知 1：男 2：女");

    /**
     * The column <code>mini_shop_471752.b2c_patient.birthday</code>. 出生年月
     */
    public final TableField<PatientRecord, Date> BIRTHDAY = createField("birthday", org.jooq.impl.SQLDataType.DATE, this, "出生年月");

    /**
     * The column <code>mini_shop_471752.b2c_patient.disease_history</code>. 过往病史：null:未知，"":无，其他逗号隔开
     */
    public final TableField<PatientRecord, String> DISEASE_HISTORY = createField("disease_history", org.jooq.impl.SQLDataType.VARCHAR(64), this, "过往病史：null:未知，\"\":无，其他逗号隔开");

    /**
     * The column <code>mini_shop_471752.b2c_patient.allergy_history</code>. 过敏史:null:未知，"":无
     */
    public final TableField<PatientRecord, String> ALLERGY_HISTORY = createField("allergy_history", org.jooq.impl.SQLDataType.VARCHAR(64), this, "过敏史:null:未知，\"\":无");

    /**
     * The column <code>mini_shop_471752.b2c_patient.family_disease_history</code>. 家族病史：null:未知，"":无，其他逗号隔开
     */
    public final TableField<PatientRecord, String> FAMILY_DISEASE_HISTORY = createField("family_disease_history", org.jooq.impl.SQLDataType.VARCHAR(64), this, "家族病史：null:未知，\"\":无，其他逗号隔开");

    /**
     * The column <code>mini_shop_471752.b2c_patient.gestation_type</code>. 妊娠哺乳状态:0:未知，1：无，2：备孕中，3：怀孕中，4：正在哺乳
     */
    public final TableField<PatientRecord, Byte> GESTATION_TYPE = createField("gestation_type", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "妊娠哺乳状态:0:未知，1：无，2：备孕中，3：怀孕中，4：正在哺乳");

    /**
     * The column <code>mini_shop_471752.b2c_patient.kidney_function_ok</code>. 肾功能:0:未知，1：正常，2：异常
     */
    public final TableField<PatientRecord, Byte> KIDNEY_FUNCTION_OK = createField("kidney_function_ok", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "肾功能:0:未知，1：正常，2：异常");

    /**
     * The column <code>mini_shop_471752.b2c_patient.liver_function_ok</code>. 肝功能:0:未知，1：正常，2：异常
     */
    public final TableField<PatientRecord, Byte> LIVER_FUNCTION_OK = createField("liver_function_ok", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "肝功能:0:未知，1：正常，2：异常");

    /**
     * The column <code>mini_shop_471752.b2c_patient.is_fetch</code>. 该患者是否拉取过个人信息
     */
    public final TableField<PatientRecord, Byte> IS_FETCH = createField("is_fetch", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "该患者是否拉取过个人信息");

    /**
     * The column <code>mini_shop_471752.b2c_patient.remarks</code>. 介绍
     */
    public final TableField<PatientRecord, String> REMARKS = createField("remarks", org.jooq.impl.SQLDataType.CLOB, this, "介绍");

    /**
     * The column <code>mini_shop_471752.b2c_patient.is_delete</code>.
     */
    public final TableField<PatientRecord, Byte> IS_DELETE = createField("is_delete", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_patient.create_time</code>.
     */
    public final TableField<PatientRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_patient.update_time</code>. 最后修改时间
     */
    public final TableField<PatientRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "最后修改时间");

    /**
     * Create a <code>mini_shop_471752.b2c_patient</code> table reference
     */
    public Patient() {
        this(DSL.name("b2c_patient"), null);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_patient</code> table reference
     */
    public Patient(String alias) {
        this(DSL.name(alias), PATIENT);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_patient</code> table reference
     */
    public Patient(Name alias) {
        this(alias, PATIENT);
    }

    private Patient(Name alias, Table<PatientRecord> aliased) {
        this(alias, aliased, null);
    }

    private Patient(Name alias, Table<PatientRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("患者信息"));
    }

    public <O extends Record> Patient(Table<O> child, ForeignKey<O, PatientRecord> key) {
        super(child, key, PATIENT);
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
        return Arrays.<Index>asList(Indexes.PATIENT_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<PatientRecord, Integer> getIdentity() {
        return Keys.IDENTITY_PATIENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<PatientRecord> getPrimaryKey() {
        return Keys.KEY_B2C_PATIENT_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PatientRecord>> getKeys() {
        return Arrays.<UniqueKey<PatientRecord>>asList(Keys.KEY_B2C_PATIENT_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Patient as(String alias) {
        return new Patient(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Patient as(Name alias) {
        return new Patient(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Patient rename(String name) {
        return new Patient(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Patient rename(Name name) {
        return new Patient(name, null);
    }
}