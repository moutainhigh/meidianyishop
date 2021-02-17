package com.meidianyi.shop.dao.foundation.base;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.Page;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.api.ApiPageResult;
import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import com.meidianyi.shop.dao.foundation.database.DatabaseManager;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectLimitStep;
import org.jooq.impl.DefaultDSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * 
 * @author lixinguo
 *
 */
public abstract  class AbstractCommonBaseDao {

	@Autowired
	protected DatabaseManager databaseManager;


	/**
	 * 当前DB
	 * @return
	 */
	protected abstract DefaultDSLContext db();

	/**
	 * 得到分页结果
	 * 
	 * @param <T>
	 * @param select
	 * @param currentPage
	 * @param pageRows
	 * @param clazz
	 * @return
	 */
	public <T> PageResult<T> getPageResult(SelectLimitStep<?> select, Integer currentPage, Integer pageRows,
			Class<T> clazz) {
		Integer totalRows = db().fetchCount(select);
		PageResult<T> pageResult = new PageResult<>();
		pageResult.page = Page.getPage(totalRows, currentPage, pageRows);
		Result<?> result = select
				.limit((pageResult.page.currentPage - 1) * pageResult.page.pageRows, pageResult.page.pageRows).fetch();
		pageResult.dataList = result.into(clazz);
		return pageResult;
	}

	public <T> PageResult<T> getPageResult(SelectLimitStep<?> select, BasePageParam param, Class<T> clazz) {
		return getPageResult(select, param.getCurrentPage(), param.getPageRows(), clazz);
	}

	public <T> PageResult<T> getPageResult(SelectLimitStep<?> select, Integer currentPage, Class<T> clazz) {
		return getPageResult(select, currentPage, 20, clazz);
	}

	public <T> PageResult<T> getPageResult(SelectLimitStep<?> select, Class<T> clazz) {
		return getPageResult(select, 1, 20, clazz);
	}

	/**
	 * 复制from到jooq的记录对象中，不含值为null的字段
	 * 
	 * @param from
	 * @param to
	 */
	public void assign(Object from, Record to) {
		FieldsUtil.assignNotNull(from, to);
	}

	/**
	 * 复制from到jooq的记录对象中，不含值为null的字段
	 * 
	 * @param from
	 * @param to
	 * @param onlyFields
	 */
	public void assign(Object from, Record to, String[] onlyFields) {
		FieldsUtil.assignNotNull(from, to, Arrays.asList(onlyFields));
	}

	public String likeValue(String val) {
		return "%" + likeReplace(val) + "%";
	}

	protected String likeReplace(String val) {
		val = val.replaceAll("%", "\\%");
		return val.replaceAll("_", "\\_");
	}

	public String prefixLikeValue(String val) {
		return likeReplace(val) + "%";
	}

	public String suffixLikeValue(String val) {
		return "%" + likeReplace(val);
	}

	public String getDelPrefix(Integer recordId) {
		if (recordId == null) {
			return null;
		}
		return DelFlag.DEL_ITEM_PREFIX + recordId + DelFlag.DEL_ITEM_SPLITER;
	}

	protected Logger logger() {
		return LoggerFactory.getLogger(this.getClass());
	}


    /**
     * 对外接口分页方法
     * @param select
     * @param currentPage
     * @param pageRows
     * @param clazz
     * @param <T>
     */
    public <T> ApiPageResult<T> getApiPageResult(SelectLimitStep<?> select, Integer currentPage, Integer pageRows,
                                                 Class<T> clazz){
        PageResult<T> pageResult = getPageResult(select, currentPage, pageRows, clazz);

        ApiPageResult<T> apiPageResult = new ApiPageResult<>();
        apiPageResult.setCurPageNo(pageResult.page.currentPage);
        apiPageResult.setPageSize(pageResult.page.pageCount);
        apiPageResult.setTotalCount(pageResult.page.totalRows);
        apiPageResult.setDataList(pageResult.getDataList());

        return apiPageResult;
    }
}
