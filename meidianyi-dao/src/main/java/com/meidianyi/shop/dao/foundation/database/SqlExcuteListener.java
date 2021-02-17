package com.meidianyi.shop.dao.foundation.database;

import static java.lang.Boolean.TRUE;
import static org.jooq.impl.DSL.val;
import static org.jooq.tools.StringUtils.abbreviate;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.Utils;
import org.jooq.Configuration;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteType;
import org.jooq.Param;
import org.jooq.QueryPart;
import org.jooq.VisitContext;
import org.jooq.VisitListener;
import org.jooq.VisitListenerProvider;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.impl.DefaultVisitListener;
import org.jooq.impl.DefaultVisitListenerProvider;
import org.jooq.tools.JooqLogger;
import org.springframework.beans.factory.annotation.Autowired;

import com.meidianyi.shop.config.DatabaseConfig;
import com.meidianyi.shop.support.SpringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author 新国
 *
 */
@Slf4j
public class SqlExcuteListener extends DefaultExecuteListener {

	private static final JooqLogger LOGGER = JooqLogger.getLogger(SqlExcuteListener.class);

	private static final long serialVersionUID = 1L;

	private static final int BIND_PARAM_MAX_LENGTH = 2000;

	@Override
	public void renderEnd(ExecuteContext ctx) {

		if (LOGGER.isDebugEnabled()) {
			Configuration configuration = ctx.configuration();
			String newline = TRUE.equals(configuration.settings().isRenderFormatted()) ? "\n" : "";

			// [#2939] Prevent excessive logging of bind variables only in DEBUG mode, not
			// in TRACE mode.
			if (!LOGGER.isTraceEnabled()) {
				configuration = abbreviateBindVariables(configuration);
			}
			String[] batchSql = ctx.batchSQL();
			if (ctx.query() != null) {

				// Actual SQL passed to JDBC
//				LOGGER.debug("Executing query", newline + ctx.sql());

				// [#1278] DEBUG log also SQL with inlined bind values, if
				// that is not the same as the actual SQL passed to JDBC
				String inlined = DSL.using(configuration).renderInlined(ctx.query());
				if (!ctx.sql().equals(inlined)) {
					this.logSql(newline + inlined);
				}
			}

			// [#2987] Log routines
			else if (ctx.routine() != null) {
//				LOGGER.debug("Calling routine", newline + ctx.sql());
				String inlined = DSL.using(configuration).renderInlined(ctx.routine());
				if (!ctx.sql().equals(inlined)) {
					this.logSql(newline + inlined);
				}
			}

			else if (!StringUtils.isBlank(ctx.sql())) {

				// [#1529] Batch queries should be logged specially
				if (ctx.type() == ExecuteType.BATCH) {
					LOGGER.debug("Executing batch query", newline + ctx.sql());
				} else {
					LOGGER.debug("Executing query", newline + ctx.sql());
				}
			}

			// [#2532] Log a complete BatchMultiple query
			else if (batchSql.length > 0) {
				if (batchSql[batchSql.length - 1] != null) {
					for (String sql : batchSql) {
						this.logSql(newline + sql);
					}
				}
			}
		}

	}

	private void logSql(String sql) {
		LOGGER.debug(sql);
//		String message = sql;
//		DatabaseConfig databaseConfig = SpringUtil.getBean(DatabaseConfig.class);
//		String mainDbName = databaseConfig.getDatabase();
//		String shopDbPrefix = databaseConfig.getShopDbPrefix();
//		String dbName = "";
//		String search = "`" + mainDbName + "`.";
//		if (StringUtils.containsAny(sql, search)) {
//			dbName = mainDbName;
//			message = RegExUtils.replaceAll(sql, search, "");
//		}
//		search = "`(" + shopDbPrefix + "_\\d+)\\.`";
//		Matcher m = Pattern.compile(search, Pattern.CASE_INSENSITIVE).matcher(sql);
//		if (m.find()) {
//			dbName = m.group(1);
//			search = "`" + dbName + "`.";
//			message = RegExUtils.replaceAll(sql, search, "");
//		}
//		LOGGER.debug("[" + dbName + "]\t" + message);
	}

	/**
	 * Add a {@link VisitListener} that transforms all bind variables by
	 * abbreviating them.
	 */
	private final Configuration abbreviateBindVariables(Configuration configuration) {
		VisitListenerProvider[] oldProviders = configuration.visitListenerProviders();
		VisitListenerProvider[] newProviders = new VisitListenerProvider[oldProviders.length + 1];
		System.arraycopy(oldProviders, 0, newProviders, 0, oldProviders.length);
		newProviders[newProviders.length - 1] = new DefaultVisitListenerProvider(new BindValueAbbreviator());

		return configuration.derive(newProviders);
	}

	private static class BindValueAbbreviator extends DefaultVisitListener {

		private boolean anyAbbreviations = false;

		@Override
		public void visitStart(VisitContext context) {
			if (context.renderContext() != null) {
				QueryPart part = context.queryPart();

				if (part instanceof Param<?>) {
					Param<?> param = (Param<?>) part;
					Object value = param.getValue();

					if (value instanceof String && ((String) value).length() > BIND_PARAM_MAX_LENGTH) {
						anyAbbreviations = true;
						context.queryPart(val(abbreviate((String) value, BIND_PARAM_MAX_LENGTH)));
					} else if (value instanceof byte[] && ((byte[]) value).length > BIND_PARAM_MAX_LENGTH) {
						anyAbbreviations = true;
						context.queryPart(val(Arrays.copyOf((byte[]) value, BIND_PARAM_MAX_LENGTH)));
					}
				}
			}
		}

		@Override
		public void visitEnd(VisitContext context) {
			if (anyAbbreviations) {
				if (context.queryPartsLength() == 1) {
					context.renderContext().sql(
							" -- Bind values may have been abbreviated for DEBUG logging. Use TRACE logging for very large bind variables.");
				}
			}
		}
	}

}
