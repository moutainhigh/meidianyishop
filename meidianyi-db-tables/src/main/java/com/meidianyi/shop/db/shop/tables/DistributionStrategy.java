/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.db.shop.tables;


import com.meidianyi.shop.db.shop.Indexes;
import com.meidianyi.shop.db.shop.Keys;
import com.meidianyi.shop.db.shop.MiniShop_471752;
import com.meidianyi.shop.db.shop.tables.records.DistributionStrategyRecord;

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
public class DistributionStrategy extends TableImpl<DistributionStrategyRecord> {

    private static final long serialVersionUID = 2027354632;

    /**
     * The reference instance of <code>jmini_shop_471752.b2c_distribution_strategy</code>
     */
    public static final DistributionStrategy DISTRIBUTION_STRATEGY = new DistributionStrategy();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DistributionStrategyRecord> getRecordType() {
        return DistributionStrategyRecord.class;
    }

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.id</code>.
     */
    public final TableField<DistributionStrategyRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.strategy_name</code>. 策略名称
     */
    public final TableField<DistributionStrategyRecord, String> STRATEGY_NAME = createField("strategy_name", org.jooq.impl.SQLDataType.VARCHAR(120).nullable(false), this, "策略名称");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.strategy_level</code>. 策略等级
     */
    public final TableField<DistributionStrategyRecord, Byte> STRATEGY_LEVEL = createField("strategy_level", org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "策略等级");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.start_time</code>. 开始时间
     */
    public final TableField<DistributionStrategyRecord, Timestamp> START_TIME = createField("start_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "开始时间");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.end_time</code>. 结束时间
     */
    public final TableField<DistributionStrategyRecord, Timestamp> END_TIME = createField("end_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "结束时间");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.self_purchase</code>. 自购返利
     */
    public final TableField<DistributionStrategyRecord, Byte> SELF_PURCHASE = createField("self_purchase", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "自购返利");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.cost_protection</code>. 成本保护
     */
    public final TableField<DistributionStrategyRecord, Byte> COST_PROTECTION = createField("cost_protection", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "成本保护");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.fanli_ratio</code>. 返利比例（%的系数）
     */
    public final TableField<DistributionStrategyRecord, Double> FANLI_RATIO = createField("fanli_ratio", org.jooq.impl.SQLDataType.FLOAT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.FLOAT)), this, "返利比例（%的系数）");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.rebate_ratio</code>. 间接
     */
    public final TableField<DistributionStrategyRecord, Double> REBATE_RATIO = createField("rebate_ratio", org.jooq.impl.SQLDataType.FLOAT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.FLOAT)), this, "间接");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.fanli_ratio_2</code>. 二级返利比例（%的系数）
     */
    public final TableField<DistributionStrategyRecord, Double> FANLI_RATIO_2 = createField("fanli_ratio_2", org.jooq.impl.SQLDataType.FLOAT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.FLOAT)), this, "二级返利比例（%的系数）");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.rebate_ratio_2</code>. 间接
     */
    public final TableField<DistributionStrategyRecord, Double> REBATE_RATIO_2 = createField("rebate_ratio_2", org.jooq.impl.SQLDataType.FLOAT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.FLOAT)), this, "间接");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.fanli_ratio_3</code>. 三级返利比例（%的系数）
     */
    public final TableField<DistributionStrategyRecord, Double> FANLI_RATIO_3 = createField("fanli_ratio_3", org.jooq.impl.SQLDataType.FLOAT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.FLOAT)), this, "三级返利比例（%的系数）");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.rebate_ratio_3</code>. 间接
     */
    public final TableField<DistributionStrategyRecord, Double> REBATE_RATIO_3 = createField("rebate_ratio_3", org.jooq.impl.SQLDataType.FLOAT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.FLOAT)), this, "间接");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.fanli_ratio_4</code>. 四级返利比例（%的系数）
     */
    public final TableField<DistributionStrategyRecord, Double> FANLI_RATIO_4 = createField("fanli_ratio_4", org.jooq.impl.SQLDataType.FLOAT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.FLOAT)), this, "四级返利比例（%的系数）");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.rebate_ratio_4</code>. 间接
     */
    public final TableField<DistributionStrategyRecord, Double> REBATE_RATIO_4 = createField("rebate_ratio_4", org.jooq.impl.SQLDataType.FLOAT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.FLOAT)), this, "间接");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.fanli_ratio_5</code>. 五级返利比例（%的系数）
     */
    public final TableField<DistributionStrategyRecord, Double> FANLI_RATIO_5 = createField("fanli_ratio_5", org.jooq.impl.SQLDataType.FLOAT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.FLOAT)), this, "五级返利比例（%的系数）");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.rebate_ratio_5</code>. 间接
     */
    public final TableField<DistributionStrategyRecord, Double> REBATE_RATIO_5 = createField("rebate_ratio_5", org.jooq.impl.SQLDataType.FLOAT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.FLOAT)), this, "间接");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.first_rebate</code>. 邀请新用户下首单返利
     */
    public final TableField<DistributionStrategyRecord, Byte> FIRST_REBATE = createField("first_rebate", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "邀请新用户下首单返利");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.first_ratio</code>. 首单返利金额
     */
    public final TableField<DistributionStrategyRecord, Double> FIRST_RATIO = createField("first_ratio", org.jooq.impl.SQLDataType.FLOAT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.FLOAT)), this, "首单返利金额");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.first_ratio_2</code>. 首单返利金额
     */
    public final TableField<DistributionStrategyRecord, Double> FIRST_RATIO_2 = createField("first_ratio_2", org.jooq.impl.SQLDataType.FLOAT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.FLOAT)), this, "首单返利金额");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.first_ratio_3</code>. 首单返利金额
     */
    public final TableField<DistributionStrategyRecord, Double> FIRST_RATIO_3 = createField("first_ratio_3", org.jooq.impl.SQLDataType.FLOAT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.FLOAT)), this, "首单返利金额");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.first_ratio_4</code>. 首单返利金额
     */
    public final TableField<DistributionStrategyRecord, Double> FIRST_RATIO_4 = createField("first_ratio_4", org.jooq.impl.SQLDataType.FLOAT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.FLOAT)), this, "首单返利金额");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.first_ratio_5</code>. 首单返利金额
     */
    public final TableField<DistributionStrategyRecord, Double> FIRST_RATIO_5 = createField("first_ratio_5", org.jooq.impl.SQLDataType.FLOAT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.FLOAT)), this, "首单返利金额");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.recommend_type</code>. 0:全部商品1:部分商品
     */
    public final TableField<DistributionStrategyRecord, Byte> RECOMMEND_TYPE = createField("recommend_type", org.jooq.impl.SQLDataType.TINYINT, this, "0:全部商品1:部分商品");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.recommend_goods_id</code>. 返利商品ids
     */
    public final TableField<DistributionStrategyRecord, String> RECOMMEND_GOODS_ID = createField("recommend_goods_id", org.jooq.impl.SQLDataType.CLOB, this, "返利商品ids");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.recommend_cat_id</code>. 返利分类ids
     */
    public final TableField<DistributionStrategyRecord, String> RECOMMEND_CAT_ID = createField("recommend_cat_id", org.jooq.impl.SQLDataType.CLOB, this, "返利分类ids");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.status</code>. 1停用
     */
    public final TableField<DistributionStrategyRecord, Byte> STATUS = createField("status", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "1停用");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.del_flag</code>. 1删除
     */
    public final TableField<DistributionStrategyRecord, Byte> DEL_FLAG = createField("del_flag", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "1删除");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.del_time</code>. 删除时间
     */
    public final TableField<DistributionStrategyRecord, Timestamp> DEL_TIME = createField("del_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "删除时间");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.recommend_sort_id</code>. 返利商家分类ids
     */
    public final TableField<DistributionStrategyRecord, String> RECOMMEND_SORT_ID = createField("recommend_sort_id", org.jooq.impl.SQLDataType.VARCHAR(300), this, "返利商家分类ids");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.send_coupon</code>. 赠送优惠券
     */
    public final TableField<DistributionStrategyRecord, Byte> SEND_COUPON = createField("send_coupon", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "赠送优惠券");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.create_time</code>.
     */
    public final TableField<DistributionStrategyRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.update_time</code>. 最后修改时间
     */
    public final TableField<DistributionStrategyRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "最后修改时间");

    /**
     * The column <code>jmini_shop_471752.b2c_distribution_strategy.strategy_type</code>. 佣金计算方式;0:商品实际支付金额*佣金比例；1：商品实际利润（实际支付金额-成本价）* 佣金比例
     */
    public final TableField<DistributionStrategyRecord, Byte> STRATEGY_TYPE = createField("strategy_type", org.jooq.impl.SQLDataType.TINYINT.defaultValue(DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "佣金计算方式;0:商品实际支付金额*佣金比例；1：商品实际利润（实际支付金额-成本价）* 佣金比例");

    /**
     * Create a <code>jmini_shop_471752.b2c_distribution_strategy</code> table reference
     */
    public DistributionStrategy() {
        this(DSL.name("b2c_distribution_strategy"), null);
    }

    /**
     * Create an aliased <code>jmini_shop_471752.b2c_distribution_strategy</code> table reference
     */
    public DistributionStrategy(String alias) {
        this(DSL.name(alias), DISTRIBUTION_STRATEGY);
    }

    /**
     * Create an aliased <code>jmini_shop_471752.b2c_distribution_strategy</code> table reference
     */
    public DistributionStrategy(Name alias) {
        this(alias, DISTRIBUTION_STRATEGY);
    }

    private DistributionStrategy(Name alias, Table<DistributionStrategyRecord> aliased) {
        this(alias, aliased, null);
    }

    private DistributionStrategy(Name alias, Table<DistributionStrategyRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> DistributionStrategy(Table<O> child, ForeignKey<O, DistributionStrategyRecord> key) {
        super(child, key, DISTRIBUTION_STRATEGY);
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
        return Arrays.<Index>asList(Indexes.DISTRIBUTION_STRATEGY_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<DistributionStrategyRecord, Integer> getIdentity() {
        return Keys.IDENTITY_DISTRIBUTION_STRATEGY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<DistributionStrategyRecord> getPrimaryKey() {
        return Keys.KEY_B2C_DISTRIBUTION_STRATEGY_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<DistributionStrategyRecord>> getKeys() {
        return Arrays.<UniqueKey<DistributionStrategyRecord>>asList(Keys.KEY_B2C_DISTRIBUTION_STRATEGY_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionStrategy as(String alias) {
        return new DistributionStrategy(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistributionStrategy as(Name alias) {
        return new DistributionStrategy(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DistributionStrategy rename(String name) {
        return new DistributionStrategy(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DistributionStrategy rename(Name name) {
        return new DistributionStrategy(name, null);
    }
}