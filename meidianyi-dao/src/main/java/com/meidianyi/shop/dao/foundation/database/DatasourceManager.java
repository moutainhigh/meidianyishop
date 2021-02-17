package com.meidianyi.shop.dao.foundation.database;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.meidianyi.shop.config.DatabaseConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lixinguo
 *
 */
@Service
@Data
public class DatasourceManager {

    @Autowired
    protected DatabaseConfig databaseConfig;

    /**
     * 数据源列表，多线程共用。
     */
    protected Map<String, DataSource> datasources = Collections
        .synchronizedMap(new HashMap<String, DataSource>());

    /**
     * 得到主数据库配置
     *
     * @return
     */
    public DbConfig getMainDbConfig() {
        return new DbConfig(databaseConfig.getHost(), databaseConfig.getPort(), databaseConfig.getDatabase(),
            databaseConfig.getUsername(), databaseConfig.getPassword());
    }

    /**
     * 得到主库数据源
     */
    public DataSource getMainDbDatasource() {
        return this.getDatasource(getMainDbConfig());
    }

    /**
     * 得到即将创建店铺库的数据源
     */
    public DataSource getToCreateShopDbDatasource() {
        return this.getDatasource(new DbConfig(databaseConfig.getShopHost(), databaseConfig.getShopPort(), "",
            databaseConfig.getShopUsername(), databaseConfig.getShopPassword()));
    }

    /**
     * 得到数据源
     */
    protected DataSource getDatasource(DbConfig dbConfig) {
        String key = dbConfig.getDatasourceKey();
        if (!datasources.containsKey(key)) {
            datasources.put(key,dataSource(getJdbcUrl(dbConfig.host, dbConfig.port, ""), dbConfig.username, dbConfig.password));
        }
        return datasources.get(key);
    }


    /**
     * 得到配置好的数据源
     *
     * @param url
     * @param username
     * @param password
     * @return
     */
    protected DataSource dataSource(String url, String username, String password) {
        Map<String,String> map =new HashMap<>();
        map.put("url",url);
        map.put("username",username);
        map.put("password",password);
        map.put("driverClassName",databaseConfig.getDriver());
        map.put("timeBetweenLogStatsMillis","111");
        if (databaseConfig.getMaxPoolSize() > 0) {
            map.put("maxActive",databaseConfig.getMaxPoolSize()+"");
        }
        if (databaseConfig.getMinIdle() > 0) {
            map.put("minIdle",databaseConfig.getMinIdle()+"");
        }
        map.put("filters","mergeStat");
        DataSource dataSource = null;
        try {
            dataSource = DruidDataSourceFactory.createDataSource(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSource;
    }


    /**
     * 得到JDBC串
     *
     * @param host
     * @param port
     * @param database
     * @return
     */
    public String getJdbcUrl(String host, Integer port, String database) {
        return "jdbc:mysql://" + host + ":" + port + "/" + database
            + "?serverTimezone=Hongkong&useSSL=false&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull";
    }

    /**
     * 得到安装数据库配置
     *
     * @param shopId
     * @return
     */
    public DbConfig getInstallShopDbConfig(Integer shopId) {
        return new DbConfig(databaseConfig.getShopHost(), databaseConfig.getShopPort(),
            databaseConfig.getShopDbPrefix() + shopId.toString(), databaseConfig.getShopUsername(),
            databaseConfig.getShopPassword());
    }
}
