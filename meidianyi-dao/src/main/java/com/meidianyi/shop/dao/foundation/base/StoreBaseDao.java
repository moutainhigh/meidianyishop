package com.meidianyi.shop.dao.foundation.base;

import org.jooq.Configuration;
import org.jooq.ContextTransactionalRunnable;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.SQLDataType;

import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author chenjie
 * @date 2020年08月24日
 */
public class StoreBaseDao extends AbstractCommonBaseDao {

    /**
     * Shop DB连接事务配置，线程内单例
     */
    private static final ThreadLocal<Deque<Configuration>> STORE_DB_CONFIGURATION = ThreadLocal.withInitial(ArrayDeque<Configuration>::new);


    /**
     * 当前店铺连接
     */
    @Override
    protected DefaultDSLContext db() {
        Deque<Configuration> config = STORE_DB_CONFIGURATION.get();
        if (config.peek() != null) {
            return (DefaultDSLContext) DSL.using(config.peek());
        }
        return databaseManager.currentShopDb();
    }

    /**
     * 事务
     *
     * @param transactional
     */
    public void transaction(ContextTransactionalRunnable transactional) {
        db().transaction((configuration) -> {
            Deque<Configuration> config = STORE_DB_CONFIGURATION.get();
            config.push(configuration);
            try {
                transactional.run();
            } finally {
                config.pop();
            }
        });
    }

    /**
     * 当前店铺Id
     *
     * @return
     */
    public Integer getShopId() {
        return databaseManager.getCurrentShopId();
    }



    public Field<String> dateFormat(Field<Timestamp> field, String format) {
        return DSL.field("date_format({0}, {1})", SQLDataType.VARCHAR,
            field, DSL.inline(format));
    }

}
