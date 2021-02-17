package com.meidianyi.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 
 * @author lixinguo
 *
 */
@Configuration
@Data
public class DatabaseConfig {
	@Value(value = "${db.host}")
	protected String host;

	@Value(value = "${db.port}")
	protected Integer port;

	@Value(value = "${db.database}")
	protected String database;

	@Value(value = "${db.username}")
	protected String username;

	@Value(value = "${db.password}")
	protected String password;

	@Value(value = "${db.prefix}")
	protected String dbPrefix;

	@Value(value = "${db.shop.host}")
	protected String shopHost;

	@Value(value = "${db.shop.port}")
	protected Integer shopPort;

	@Value(value = "${db.shop.username}")
	protected String shopUsername;

	@Value(value = "${db.shop.password}")
	protected String shopPassword;

	@Value(value = "${db.shop.prefix}")
	protected String shopDbPrefix;

	protected String driver = "com.mysql.cj.jdbc.Driver";
	protected String dialect = "MYSQL";

	@Value(value = "${db.pool.max-pool-size:-1}")
	protected int maxPoolSize;
	
	@Value(value = "${db.pool.min-idle:-1}")
	protected int minIdle;

}
