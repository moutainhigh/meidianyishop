package com.meidianyi.shop.service.shop.decoration;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.PageClassification;
import com.meidianyi.shop.db.shop.tables.records.PageClassificationRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.decoration.PageCategoryListQueryParam;
import com.meidianyi.shop.service.pojo.shop.decoration.PageClassificationPojo;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.meidianyi.shop.db.shop.tables.PageClassification.PAGE_CLASSIFICATION;
import static com.meidianyi.shop.db.shop.tables.XcxCustomerPage.XCX_CUSTOMER_PAGE;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;



/**
 *
 * @author lixinguo
 *
 */
@Service

public class PageClassificationService extends ShopBaseService {

	/**
	 * 装修页面列表
     *
	 * @param param
	 * @return
	 */
	public PageResult<PageClassificationPojo> getPageList(PageCategoryListQueryParam param) {
		SelectWhereStep<Record> select = db().select().from(PAGE_CLASSIFICATION);
		select = this.buildOptions(select, param);
		select.orderBy(PAGE_CLASSIFICATION.CREATE_TIME.desc());
		return this.getPageResult(select, param.currentPage,param.pageRows,PageClassificationPojo.class);
	}

	/**
	 * 查询条件
     *
	 * @param select
	 * @param param
	 * @return
	 */
	public SelectWhereStep<Record> buildOptions(SelectWhereStep<Record> select, PageCategoryListQueryParam param) {
		if (param == null) {
			return select;
		}

		if (!StringUtils.isBlank(param.keywords)) {
			select.where(PAGE_CLASSIFICATION.NAME.like(this.likeValue(param.keywords)));
		}

		return select;
	}

	/**
	 * 获取分类Map
     *
	 * @return
	 */
	public Map<String, String> getClassificationMap() {
		Map<String, String> result = new HashMap<String, String>(20);
		for (PageClassificationRecord r : getAll()) {
			result.put(r.getId().toString(), r.getName());
		}
		return result;
	}

	/**
	 * 得到所有页面分类
     *
	 * @return
	 */
	public Result<PageClassificationRecord> getAll() {
		return db().fetch(PAGE_CLASSIFICATION);
	}

	/**
	 * 得到id对应分类
     *
	 * @param id
	 * @return
	 */
	public PageClassificationRecord getRow(Integer id) {
		return db().fetchAny(PAGE_CLASSIFICATION, PAGE_CLASSIFICATION.ID.eq((id)));
	}

	public PageClassificationRecord getRowByName(String name) {
		return db().fetchAny(PAGE_CLASSIFICATION, PAGE_CLASSIFICATION.NAME.eq(name));
	}

	/**
	 * 得到名称相同，但ID不同的一条分类记录
     *
	 * @param notId
	 * @param name
	 * @return
	 */
	public PageClassificationRecord getNoIdRow(Integer notId, String name) {
		return db().fetchAny(PAGE_CLASSIFICATION,
				PAGE_CLASSIFICATION.NAME.eq(name).and(PAGE_CLASSIFICATION.ID.eq((notId))));
	}

	public int removeRow(Integer id) {
		return db().delete(PAGE_CLASSIFICATION).where(PAGE_CLASSIFICATION.ID.eq((id))).execute();
	}

	public int setName(Integer id, String name) {
		return db().update(PAGE_CLASSIFICATION)
				.set(PAGE_CLASSIFICATION.NAME, name)
				.where(PAGE_CLASSIFICATION.ID.eq((id)))
				.execute();
	}

	public int addRow(String name) {
		return db().insertInto(PAGE_CLASSIFICATION)
				.set(PAGE_CLASSIFICATION.NAME, name)
				.execute();
	}

	/**
	 *
	 * @param pageId 若不涉及，置为-1
	 * @param pageName 若不涉及，置为""
	 * @return 存在返回true，不存在返回false
	 */
	public boolean checkExist(int pageId,String pageName){
		Condition idCondition = PageClassification.PAGE_CLASSIFICATION.ID.eq(pageId);
		Condition nameCondition = (PageClassification.PAGE_CLASSIFICATION.NAME.eq(pageName));
		if(db().fetchCount(PageClassification.PAGE_CLASSIFICATION,idCondition) > 0) {
			return true;
		}
		if (db().fetchCount(PageClassification.PAGE_CLASSIFICATION,nameCondition) > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 删除并重置页面分类
	 * @param pageId
	 * @return 操作成功，返回true，否false
	 */
	public boolean rmAndResetCategory(int pageId){
		int[] result = {-1};
		db().transaction(configuration->{
			DSLContext db = DSL.using(configuration);
            Condition countCondition = XCX_CUSTOMER_PAGE.CAT_ID.eq(pageId);
            if (db.fetchCount(XCX_CUSTOMER_PAGE, countCondition) > 0) {
                result[0] = db.update(XCX_CUSTOMER_PAGE)
                    .set(XCX_CUSTOMER_PAGE.CAT_ID, 0)
                    .where(XCX_CUSTOMER_PAGE.CAT_ID.eq(pageId))
						.execute();
			}
			result[0] = removeRow(pageId);

		});
		return result[0] > -1;
	}

	/**
	 * 根据页面分类id获取该类目下包含多少页面
	 * @param categoryId
	 * @return
	 */
	public int getPageCountByCategory(int categoryId){
        Condition condition = XCX_CUSTOMER_PAGE.CAT_ID.eq(categoryId).and(XCX_CUSTOMER_PAGE.PAGE_ENABLED.eq(BYTE_ONE));
        return db().fetchCount(XCX_CUSTOMER_PAGE, condition);
	}

}
