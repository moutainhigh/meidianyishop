/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.DoctorRecord;

import java.math.BigDecimal;
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
 * 医师表
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Doctor extends TableImpl<DoctorRecord> {

    private static final long serialVersionUID = -1355695510;

    /**
     * The reference instance of <code>mini_shop_471752.b2c_doctor</code>
     */
    public static final Doctor DOCTOR = new Doctor();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DoctorRecord> getRecordType() {
        return DoctorRecord.class;
    }

    /**
     * The column <code>mini_shop_471752.b2c_doctor.id</code>.
     */
    public final TableField<DoctorRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.age</code>. 年龄
     */
    public final TableField<DoctorRecord, Integer> AGE = createField("age", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "年龄");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.work_time</code>. 从业时间
     */
    public final TableField<DoctorRecord, Integer> WORK_TIME = createField("work_time", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "从业时间");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.sex</code>. 0未知 1男 2 女
     */
    public final TableField<DoctorRecord, Byte> SEX = createField("sex", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "0未知 1男 2 女");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.name</code>. 医师姓名
     */
    public final TableField<DoctorRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR(32).nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.VARCHAR)), this, "医师姓名");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.url</code>. 医师头像
     */
    public final TableField<DoctorRecord, String> URL = createField("url", org.jooq.impl.SQLDataType.VARCHAR(128).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "医师头像");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.duty</code>. 聘任职务
     */
    public final TableField<DoctorRecord, Byte> DUTY = createField("duty", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "聘任职务");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.hospital_code</code>. 医师院内编号
     */
    public final TableField<DoctorRecord, String> HOSPITAL_CODE = createField("hospital_code", org.jooq.impl.SQLDataType.VARCHAR(32).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "医师院内编号");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.certificate_code</code>. 医师资格编码
     */
    public final TableField<DoctorRecord, String> CERTIFICATE_CODE = createField("certificate_code", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "医师资格编码");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.professional_code</code>. 医师职业编码
     */
    public final TableField<DoctorRecord, String> PROFESSIONAL_CODE = createField("professional_code", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "医师职业编码");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.register_time</code>. 注册时间
     */
    public final TableField<DoctorRecord, Date> REGISTER_TIME = createField("register_time", org.jooq.impl.SQLDataType.DATE, this, "注册时间");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.register_hospital</code>. 注册医院
     */
    public final TableField<DoctorRecord, String> REGISTER_HOSPITAL = createField("register_hospital", org.jooq.impl.SQLDataType.VARCHAR(32), this, "注册医院");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.mobile</code>. 手机号
     */
    public final TableField<DoctorRecord, String> MOBILE = createField("mobile", org.jooq.impl.SQLDataType.VARCHAR(32).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "手机号");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.title_id</code>. 职称id
     */
    public final TableField<DoctorRecord, Integer> TITLE_ID = createField("title_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "职称id");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.consultation_price</code>. 问诊费用
     */
    public final TableField<DoctorRecord, BigDecimal> CONSULTATION_PRICE = createField("consultation_price", org.jooq.impl.SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(org.jooq.impl.DSL.inline("0.00", org.jooq.impl.SQLDataType.DECIMAL)), this, "问诊费用");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.treat_disease</code>. 主治疾病
     */
    public final TableField<DoctorRecord, String> TREAT_DISEASE = createField("treat_disease", org.jooq.impl.SQLDataType.VARCHAR(256).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "主治疾病");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.status</code>. 是否启用 1启用 0禁用
     */
    public final TableField<DoctorRecord, Byte> STATUS = createField("status", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.TINYINT)), this, "是否启用 1启用 0禁用");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.user_id</code>. 用户id，当前用户是否为医师
     */
    public final TableField<DoctorRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "用户id，当前用户是否为医师");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.is_delete</code>.
     */
    public final TableField<DoctorRecord, Byte> IS_DELETE = createField("is_delete", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.create_time</code>.
     */
    public final TableField<DoctorRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.update_time</code>. 最后修改时间
     */
    public final TableField<DoctorRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "最后修改时间");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.on_duty_time</code>. 上班时间
     */
    public final TableField<DoctorRecord, Timestamp> ON_DUTY_TIME = createField("on_duty_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "上班时间");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.is_on_duty</code>. 是否上班
     */
    public final TableField<DoctorRecord, Byte> IS_ON_DUTY = createField("is_on_duty", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.TINYINT)), this, "是否上班");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.can_consultation</code>. 是否接诊
     */
    public final TableField<DoctorRecord, Byte> CAN_CONSULTATION = createField("can_consultation", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.TINYINT)), this, "是否接诊");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.avg_answer_time</code>. 平均接诊时间
     */
    public final TableField<DoctorRecord, Integer> AVG_ANSWER_TIME = createField("avg_answer_time", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "平均接诊时间");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.consultation_number</code>. 接诊数
     */
    public final TableField<DoctorRecord, Integer> CONSULTATION_NUMBER = createField("consultation_number", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "接诊数");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.avg_comment_star</code>. 平均评分
     */
    public final TableField<DoctorRecord, BigDecimal> AVG_COMMENT_STAR = createField("avg_comment_star", org.jooq.impl.SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(org.jooq.impl.DSL.inline("0.00", org.jooq.impl.SQLDataType.DECIMAL)), this, "平均评分");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.attention_number</code>. 关注数
     */
    public final TableField<DoctorRecord, Integer> ATTENTION_NUMBER = createField("attention_number", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "关注数");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.user_token</code>. 医师关联用户token
     */
    public final TableField<DoctorRecord, String> USER_TOKEN = createField("user_token", org.jooq.impl.SQLDataType.VARCHAR(256).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "医师关联用户token");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.auth_time</code>. 认证时间
     */
    public final TableField<DoctorRecord, Timestamp> AUTH_TIME = createField("auth_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0000-00-00 00:00:00", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "认证时间");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.consultation_total_money</code>. 咨询总金额
     */
    public final TableField<DoctorRecord, BigDecimal> CONSULTATION_TOTAL_MONEY = createField("consultation_total_money", org.jooq.impl.SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(org.jooq.impl.DSL.inline("0.00", org.jooq.impl.SQLDataType.DECIMAL)), this, "咨询总金额");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.is_fetch</code>. 是否拉取过
     */
    public final TableField<DoctorRecord, Byte> IS_FETCH = createField("is_fetch", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "是否拉取过");

    /**
     * The column <code>mini_shop_471752.b2c_doctor.signature</code>. 医师签名
     */
    public final TableField<DoctorRecord, String> SIGNATURE = createField("signature", org.jooq.impl.SQLDataType.VARCHAR(128).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "医师签名");

    /**
     * Create a <code>mini_shop_471752.b2c_doctor</code> table reference
     */
    public Doctor() {
        this(DSL.name("b2c_doctor"), null);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_doctor</code> table reference
     */
    public Doctor(String alias) {
        this(DSL.name(alias), DOCTOR);
    }

    /**
     * Create an aliased <code>mini_shop_471752.b2c_doctor</code> table reference
     */
    public Doctor(Name alias) {
        this(alias, DOCTOR);
    }

    private Doctor(Name alias, Table<DoctorRecord> aliased) {
        this(alias, aliased, null);
    }

    private Doctor(Name alias, Table<DoctorRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("医师表"));
    }

    public <O extends Record> Doctor(Table<O> child, ForeignKey<O, DoctorRecord> key) {
        super(child, key, DOCTOR);
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
        return Arrays.<Index>asList(Indexes.DOCTOR_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<DoctorRecord, Integer> getIdentity() {
        return Keys.IDENTITY_DOCTOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<DoctorRecord> getPrimaryKey() {
        return Keys.KEY_B2C_DOCTOR_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<DoctorRecord>> getKeys() {
        return Arrays.<UniqueKey<DoctorRecord>>asList(Keys.KEY_B2C_DOCTOR_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Doctor as(String alias) {
        return new Doctor(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Doctor as(Name alias) {
        return new Doctor(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Doctor rename(String name) {
        return new Doctor(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Doctor rename(Name name) {
        return new Doctor(name, null);
    }
}
