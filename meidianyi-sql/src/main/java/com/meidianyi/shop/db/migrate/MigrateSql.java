package com.meidianyi.shop.db.migrate;

import org.flywaydb.core.api.FlywayException;

import javax.sql.DataSource;

/**
 * @author lixinguo
 */
public interface MigrateSql {


    /**
     * 是否需要升级数据库SQL版本
     *
     * @param dataSource 数据源,不包含库名
     * @param database   数据库名字
     * @param isMainDb   是否为主库，true主库，false店铺库
     * @return 是否需要升级SQL
     * @throws FlywayException 部署异常
     */
    boolean needUpdateSqlVersion(DataSource dataSource, String database, Boolean isMainDb) throws FlywayException;


    /**
     * 升级数据库SQL版本，也可以用于库新建
     *
     * @param dataSource 数据源,不包含库名
     * @param database   数据库名字
     * @param isMainDb   是否为主库，true主库，false店铺库
     * @param autoCreateDb 是否创建库
     * @throws FlywayException 部署异常
     */
    void migrateDb(DataSource dataSource, String database, Boolean isMainDb, boolean autoCreateDb) throws FlywayException;

    /**
     * 验证Db与本地Sql是否一致
     *
     * @param dataSource 数据源,不包含库名
     * @param database   数据库名字
     * @param isMainDb   是否为主库，true主库，false店铺库
     * @throws FlywayException 部署异常
     */
    void validateDb(DataSource dataSource, String database, Boolean isMainDb) throws FlywayException;
}
