package com.meidianyi.shop.service.shop.store.group;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.foundation.database.DslPlus;
import com.meidianyi.shop.db.shop.tables.records.StoreGroupRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreAccountAddParam;
import com.meidianyi.shop.service.pojo.shop.store.group.StoreGroup;
import com.meidianyi.shop.service.pojo.shop.store.group.StoreGroupQueryParam;
import org.jooq.Record;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static com.meidianyi.shop.db.shop.tables.Store.STORE;
import static com.meidianyi.shop.db.shop.tables.StoreGroup.STORE_GROUP;

/**
 * @author 王兵兵
 *
 * 2019年7月17日
 */
@Service
public class StoreGroupService extends ShopBaseService{
	/**
	 * 门店分组列表-查询
	 * @param param
	 * @return
	 */
	public PageResult<StoreGroup> getStoreGroupPageList(StoreGroupQueryParam param){
		SelectWhereStep<? extends Record> select = db()
            .select(STORE_GROUP.GROUP_ID,STORE_GROUP.GROUP_NAME,STORE_GROUP.CREATE_TIME)
            .from(STORE_GROUP);
		buildParams(select,param);
		select.orderBy(STORE_GROUP.CREATE_TIME.asc());

        PageResult<StoreGroup> res =  getPageResult(select,param.getCurrentPage(),param.getPageRows(),StoreGroup.class);
        for (StoreGroup g: res.dataList) {
            g.setNumbers(this.getGroupNumberInGroup(g.getGroupId()));
        }
        return res;
	}
	public void buildParams(SelectWhereStep<? extends  Record> select, StoreGroupQueryParam param) {
		if (param != null) {
			if (param.getGroupName() != null && !"".equals(param.getGroupName())) {
				if ( param.isNeedAccurateQuery() ){
					select.where(STORE_GROUP.GROUP_NAME.eq(param.getGroupName()));
				}else{
					select.where(STORE_GROUP.GROUP_NAME.like(this.likeValue(param.getGroupName())));
				}
			}
		}
	}

	private int getGroupNumberInGroup(int groupId){
        return db().selectCount().from(STORE).where(STORE.GROUP.eq(groupId).and(STORE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))).fetchOneInto(Integer.class);
    }

    /**
     * 门店分组-(检查组名是否可用)
     * @param param
     * @return true可用，fasle不可用
     */
    public boolean isStoreGroupExist(StoreGroupQueryParam param) {
    	param.setNeedAccurateQuery(Boolean.TRUE);
		SelectWhereStep<? extends Record> select = db().select(STORE_GROUP.GROUP_NAME)
                .from(STORE_GROUP);
		buildParams(select,param);
		return db().fetchCount(select) > 0?Boolean.FALSE:Boolean.TRUE;
    }

	/**
	 * 门店分组-新增
	 * @param param
	 * @return
	 */
	public int insertStoreGroup(StoreGroupQueryParam param) {
		StoreGroupRecord record = db().newRecord(STORE_GROUP,param);
		return  record.insert();
	}

	/**
	 * 门店分组-修改
	 * @param param
	 * @return
	 */
	public int updateStoreGroup(StoreGroupQueryParam param) {
		StoreGroupRecord record = db().newRecord(STORE_GROUP,param);
		record.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		return  record.update();
	}

	/**
	 * 门店分组-删除
	 * @param param
	 * @return
	 */
	public void deleteStoreGroup(StoreGroupQueryParam param) {
		this.transaction(()->{
			List<Integer> result = db().select(STORE.STORE_ID)
					.from(STORE)
					.where(STORE.GROUP.eq(param.getGroupId()))
					.fetch(STORE.STORE_ID);
			if ( result.size() > 0){
				db().update(STORE)
						.set(STORE.GROUP,(Integer)null)
						.where(STORE.STORE_ID.in(result))
						.execute();
			}
			db().delete(STORE_GROUP)
					.where(STORE_GROUP.GROUP_ID.eq(param.getGroupId()))
					.execute();
		});
	}

    /**
     * 全部门店分组列
     * @return
     */
    public StoreAccountAddParam getAllStoreGroup(){
        StoreAccountAddParam storeAccountAddParam = new StoreAccountAddParam();
        List<StoreGroup> storeGroups = db().select(STORE_GROUP.GROUP_ID, STORE_GROUP.GROUP_NAME, STORE_GROUP.CREATE_TIME)
            .from(STORE_GROUP).orderBy(STORE_GROUP.CREATE_TIME.asc()).fetchInto(StoreGroup.class);
        storeAccountAddParam.setStoreGroups(storeGroups);
        Integer storeId = db().select(DSL.max(STORE.STORE_ID)).from(STORE).fetchAnyInto(Integer.class);
        storeAccountAddParam.setStoreId(storeId + 1);
        return storeAccountAddParam;
    }
}
