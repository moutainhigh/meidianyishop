package com.meidianyi.shop.db.migrate.impl;

import com.meidianyi.shop.db.migrate.MigrateSql;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.MigrationState;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lixinguo
 */
@Component
public class MigrateSqlImpl implements MigrateSql {

    /**
     * flyway迁移历史表名称
     */
    static final String FLYWAY_SCHEMA_HISTORY = "flyway_schema_history";

    /**
     * 主库SQL版本位置
     */
    static final String MAIN_DB_LOCATION = "classpath:migration/main";

    /**
     * 店铺库SQL版本位置
     */
    static final String SHOP_DB_LOCATION = "classpath:migration/shop";

    /**
     * 已存在的主库数据库的基线版本，根据实际数据库SQL版本进行设置
     */
    @Value(value = "${flyway.baseLineVersion:3.3.4}")
    protected String mainBaseLineVersion;

    /**
     * 已存在的店铺库数据库的基线版本，根据实际数据库SQL版本进行设置
     */
    @Value(value = "${flyway.shopBaseLineVersion:3.3.2}")
    protected String shopBaseLineVersion;

    @Override
    public boolean needUpdateSqlVersion(DataSource dataSource, String database, Boolean isMainDb) throws FlywayException {
        Flyway flyway = config(dataSource, database, isMainDb, false).load();
        MigrationInfoService info = flyway.info();
        for (MigrationInfo migrationInfo : info.all()) {
            if (migrationInfo.getState() == MigrationState.PENDING) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void migrateDb(DataSource dataSource, String database, Boolean isMainDb, boolean autoCreateDb) throws FlywayException {
        // if db exists &&  schema_history not exists, init installed sql version list.
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        if (existsDb(jdbcTemplate, database) && !existsSchemaHistoryTable(jdbcTemplate, database)) {
            List<MigrationInfo> baselineVersionList = getBaseLineSqlVersions(dataSource, database, isMainDb);
            initSchemaHistoryTableSql(jdbcTemplate, database, baselineVersionList);
        }
        Flyway flyway = config(dataSource, database, isMainDb, autoCreateDb).load();
        flyway.migrate();
    }

    /**
     * 得到当前基线以下的SQL版本列表
     *
     * @param dataSource 数据源
     * @param database   数据库名称
     * @param isMainDb   是否为主库
     * @return 基线版本列表
     */
    private List<MigrationInfo> getBaseLineSqlVersions(DataSource dataSource, String database, Boolean isMainDb) {
        Flyway flyway = config(dataSource, database, isMainDb, false).load();
        List<MigrationInfo> result = new ArrayList<>();
        MigrationInfoService info = flyway.info();
        for (MigrationInfo migrationInfo : info.all()) {
            result.add(migrationInfo);
            if (migrationInfo.getVersion().toString().equals(isMainDb ? mainBaseLineVersion : shopBaseLineVersion)) {
                break;
            }
        }
        return result;
    }

    /**
     * 数据库是否存在
     *
     * @param jdbcTemplate jdbc模板，没有选中数据库
     * @param database     数据库名称
     * @return 数据库是否存在
     */
    private boolean existsDb(JdbcTemplate jdbcTemplate, String database) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select SCHEMA_NAME from information_schema.SCHEMATA where SCHEMA_NAME='" + database + "'");
        return list.size() > 0;
    }

    /**
     * 迁移历史表是否存在
     *
     * @param jdbcTemplate jdbc模板，没有选中数据库
     * @param database     数据库名称
     * @return 迁移历史表是否存在
     */
    private boolean existsSchemaHistoryTable(JdbcTemplate jdbcTemplate, String database) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
            "select TABLE_SCHEMA,TABLE_NAME from information_schema.tables where TABLE_SCHEMA='"
                + database + "' and TABLE_NAME='" + FLYWAY_SCHEMA_HISTORY + "'");
        return list.size() > 0;
    }

    /**
     * 创建flyway迁移表，并设置初始的SQL记录
     *
     * @param jdbcTemplate        模板
     * @param database            数据库
     * @param baselineVersionList 版本历史
     */
    private void initSchemaHistoryTableSql(JdbcTemplate jdbcTemplate, String database, List<MigrationInfo> baselineVersionList) {
        String createTableSql = "CREATE TABLE `" + database + "`.`flyway_schema_history` (\n" +
            "  `installed_rank` int(11) NOT NULL,\n" +
            "  `version` varchar(50) DEFAULT NULL,\n" +
            "  `description` varchar(200) NOT NULL,\n" +
            "  `type` varchar(20) NOT NULL,\n" +
            "  `script` varchar(1000) NOT NULL,\n" +
            "  `checksum` int(11) DEFAULT NULL,\n" +
            "  `installed_by` varchar(100) NOT NULL,\n" +
            "  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
            "  `execution_time` int(11) NOT NULL,\n" +
            "  `success` tinyint(1) NOT NULL,\n" +
            "  PRIMARY KEY (`installed_rank`),\n" +
            "  KEY `flyway_schema_history_s_idx` (`success`)\n" +
            ")";
        jdbcTemplate.execute(createTableSql);
        int rank = 1;
        for (MigrationInfo info : baselineVersionList) {
            String sql = String.format("insert into %s.%s values(%d,'%s','%s','SQL','BaseLineSql: %s',%d,'root',now(),0,1)",
                database, FLYWAY_SCHEMA_HISTORY, rank,
                info.getVersion(), info.getDescription(),
                info.getScript(), info.getChecksum());
            jdbcTemplate.execute(sql);
            rank++;
        }
    }

    @Override
    public void validateDb(DataSource dataSource, String database, Boolean isMainDb) throws FlywayException {
        Flyway flyway = config(dataSource, database, isMainDb, false).load();
        flyway.validate();
    }


    /**
     * flyway配置信息
     *
     * @param dataSource   数据源,不包含库名
     * @param database     数据库名字
     * @param isMainDb     是否为主库，true主库，false店铺库
     * @param autoCreateDb 是否自动创建库
     */
    private FluentConfiguration config(DataSource dataSource, String database, Boolean isMainDb, boolean autoCreateDb) {
        String initSql = "SET NAMES utf8mb4;\n";
        if (autoCreateDb) {
            initSql = initSql + " create database if not exists " + database
                + "  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; \n use " + database + " ;";
        }
        return Flyway.configure()
            .schemas(database)
            .initSql(initSql)
            .outOfOrder(true)
            .locations(isMainDb ? MAIN_DB_LOCATION : SHOP_DB_LOCATION)
            .dataSource(dataSource);

    }
}
