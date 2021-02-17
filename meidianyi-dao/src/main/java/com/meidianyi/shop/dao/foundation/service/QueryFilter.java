package com.meidianyi.shop.dao.foundation.service;

import lombok.extern.slf4j.Slf4j;
import org.jooq.ExecuteContext;

import com.meidianyi.shop.dao.foundation.database.SqlExcuteListener;

/**
 * SQL 监听器
 * <p>
 * 对不含 where 的 update 语句进行拦截，防止批量误操作
 *
 * @author 郑保乐
 */
public class QueryFilter extends SqlExcuteListener {

	private static final long serialVersionUID = -3172666378145934837L;

	@Override
    public void renderEnd(ExecuteContext ctx) {
        super.renderEnd(ctx);
        if (ctx.sql().matches("^(?i:(UPDATE|DELETE)(?!.* WHERE ).*)$")) {
            throw new DeleteOrUpdateWithoutWhereException();
        }
    }

    private class DeleteOrUpdateWithoutWhereException extends RuntimeException {
		private static final long serialVersionUID = 1263216828793967446L;

		@Override
        public String getMessage() {
            return "Delete or update operation without where cannot be executed.";
        }
    }
}
