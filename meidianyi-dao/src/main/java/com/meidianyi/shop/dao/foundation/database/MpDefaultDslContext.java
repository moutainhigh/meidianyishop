package com.meidianyi.shop.dao.foundation.database;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultDSLContext;


import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author lixinguo
 *
 */
public class MpDefaultDslContext extends DefaultDSLContext {

	@Getter
	@Setter
	protected DbConfig dbConfig;
	@Getter
	@Setter
	protected Integer shopId = 0;


	/**
	 *
	 */
	private static final long serialVersionUID = 2971649046754906812L;

	public MpDefaultDslContext(SQLDialect dialect) {
		super(dialect);
	}

	public MpDefaultDslContext(Configuration configuration) {
		super(configuration);
	}

	public MpDefaultDslContext(SQLDialect dialect, Settings settings) {
		super(dialect, settings);
	}

	public MpDefaultDslContext(Connection connection, SQLDialect dialect) {
		super(connection, dialect);
	}

	public MpDefaultDslContext(DataSource datasource, SQLDialect dialect) {
		super(datasource, dialect);
	}

	public MpDefaultDslContext(ConnectionProvider connectionProvider, SQLDialect dialect) {
		super(connectionProvider, dialect);
	}

	public MpDefaultDslContext(Connection connection, SQLDialect dialect, Settings settings) {
		super(connection, dialect, settings);
	}

	public MpDefaultDslContext(DataSource datasource, SQLDialect dialect, Settings settings) {
		super(datasource, dialect, settings);
	}

	public MpDefaultDslContext(ConnectionProvider connectionProvider, SQLDialect dialect, Settings settings) {
		super(connectionProvider, dialect, settings);
	}

}
