package com.meidianyi.shop.dao.foundation.database;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DatabaseConfig;
import com.meidianyi.shop.dao.foundation.service.QueryFilter;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.migrate.MigrateSql;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.jooq.ConnectionProvider;
import org.jooq.SQLDialect;
import org.jooq.TransactionProvider;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.jooq.impl.DefaultTransactionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.meidianyi.shop.db.main.tables.Shop.SHOP;

/**
 * 数据库管理，单例。需要考虑多线程互斥情况
 *
 * @author lixinguo
 *
 */
@Service
public class DatabaseManager {

	@Autowired
	protected DatabaseConfig databaseConfig;

	@Autowired
	protected DatasourceManager datasourceManager;

    @Autowired
    protected MigrateSql migrateSql;

	Logger loger = LoggerFactory.getLogger(DatabaseManager.class);

	/**
	 * 主库连接，每个线程一个连接
	 */
	private ThreadLocal<MpDefaultDslContext> mainDsl = new ThreadLocal<MpDefaultDslContext>();

	/**
	 * 店铺库连接，每个线程有一个正用的数据库连接，可以随时切换
	 */
	private ThreadLocal<MpDefaultDslContext> shopDsl = new ThreadLocal<MpDefaultDslContext>();

	/**
	 * 主库连接
	 */
	public DefaultDSLContext mainDb() {
		MpDefaultDslContext db = mainDsl.get();
		if (db == null) {
			DataSource ds = datasourceManager.getMainDbDatasource();
			db = this.getDsl(ds, datasourceManager.getMainDbConfig(), 0);
		}
		mainDsl.remove();
		mainDsl.set(db);
		return mainDsl.get();
	}

	/**
	 * 切换当前数据库连接
	 *
	 * @param shopId
	 * @return
	 */
	public DatabaseManager switchShopDb(Integer shopId) {
		loger.debug("switchShopDb===" + shopId);
		MpDefaultDslContext db = shopDsl.get();
		if (db == null || !db.getShopId().equals(shopId)) {
			ShopRecord shop = mainDb().selectFrom(SHOP).where(SHOP.SHOP_ID.eq(shopId)).fetchAny();
			if (shop != null) {
				DbConfig dbConfig = Util.parseJson(shop.getDbConfig(), DbConfig.class);
				if (dbConfig == null) {
					throw new RuntimeException("ShopId " + shopId + " Db not found");
				}
				DataSource ds = datasourceManager.getDatasource(dbConfig);
				db = getDsl(ds, dbConfig, shopId);
				shopDsl.remove();
				shopDsl.set(db);
			} else {
				throw new RuntimeException("ShopId " + shopId + " Db not found");
			}
		}
		return this;
	}

	/**
	 * 得到当前数据库店铺ID
	 *
	 * @return
	 */
	public Integer getCurrentShopId() {
		MpDefaultDslContext db = shopDsl.get();
		Assert.isTrue(db != null, "DB NULL");
		return db.getShopId();
	}

	/**
	 * 当前店铺库连接
	 */
	public MpDefaultDslContext currentShopDb() {
		MpDefaultDslContext db = shopDsl.get();
		Assert.isTrue(db != null,"db null");
		return db;
	}

	/**
	 * 从数据源获取一个连接
	 */
	protected MpDefaultDslContext getDsl(DataSource ds, DbConfig dbConfig, Integer shopId) {
		MpDefaultDslContext db = new MpDefaultDslContext(configuration(ds, dbConfig.getDatabase()));
		db.setShopId(shopId);
		db.setDbConfig(dbConfig);
		db.execute("SET NAMES utf8mb4");
		db.execute("Set sql_mode='ONLY_FULL_GROUP_BY'");
		db.execute("set global transaction isolation level read committed");
		return db;
	}

	/**
	 * 安装店铺数据库
	 */
    public boolean installShopDb(DbConfig dbConfig) {
        DataSource ds = datasourceManager.getDatasource(dbConfig);
        try{
            migrateSql.migrateDb(ds,dbConfig.getDatabase(),false,true);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

	/**
	 * 根据数据源得到JOOQ的配置
	 *
	 * @param dataSource
	 * @return
	 */
	protected DefaultConfiguration configuration(DataSource dataSource, String databaseName) {

		String jooqMainSchema = "mini_main";
		String jooqShopSchema = "mini_shop_471752";

		ConnectionProvider connectionProvider = new DataSourceConnectionProvider(dataSource);
		TransactionProvider transactionProvider = new DefaultTransactionProvider(connectionProvider);
		DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
		jooqConfiguration.set(connectionProvider);
		jooqConfiguration.set(transactionProvider);
		jooqConfiguration.set(new DefaultExecuteListenerProvider(new SqlExcuteListener()));
		SQLDialect dialect = SQLDialect.valueOf(databaseConfig.getDialect());
		jooqConfiguration.set(dialect);

		Settings settings = new Settings();

		// 设置schema映射
		if (!StringUtils.isBlank(databaseName)) {
			String inputSchema = databaseConfig.getDatabase().equals(databaseName) ? jooqMainSchema : jooqShopSchema;
			settings.withRenderMapping(new RenderMapping()
					.withSchemata(new MappedSchema().withInput(inputSchema).withOutput(databaseName)));
		}

		settings.withRenderCatalog(false);
		jooqConfiguration.setSettings(settings);
		// 不安全操作拦截器
		QueryFilter queryFilter = new QueryFilter();
		jooqConfiguration.set(queryFilter);
		return jooqConfiguration;
	}

	/**
	 * 得到当前线程店铺Db名称
	 *
	 * @return
	 */
	public String getCurrentShopDbSchema() {
		return shopDsl.get() != null ? shopDsl.get().getDbConfig().getDatabase() : "";
	}

	/**
	 * 得到当前线程店铺Db名称
	 *
	 * @return
	 */
	public String getMainDbSchema() {
		return databaseConfig.getDatabase();
	}
}
