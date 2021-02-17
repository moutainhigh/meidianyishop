package com.meidianyi.shop.service.shop.store.postsale;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.store.technician.ServiceTechnicianGroup;
import com.meidianyi.shop.service.pojo.shop.store.technician.ServiceTechnicianGroupParam;
import com.meidianyi.shop.service.pojo.shop.store.technician.TechnicianGroupPageListParam;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.SERVICE_TECHNICIAN_GROUP;
import static org.apache.commons.lang3.math.NumberUtils.SHORT_ZERO;


/**
 * @author 黄荣刚
 * @date 2019年7月15日
 *
 */
@Service

public class ServiceTechnicianGroupService extends ShopBaseService {
	/** 没有被删除的 */
	public final static short NORMAL =0 ;
	/** 已经被删除的 */
	public final static short DISABLE = 1;

	public ServiceTechnicianGroup select(Integer id) {
		return db().select(SERVICE_TECHNICIAN_GROUP.GROUP_ID, SERVICE_TECHNICIAN_GROUP.GROUP_NAME, SERVICE_TECHNICIAN_GROUP.STORE_ID, SERVICE_TECHNICIAN_GROUP.CREATE_TIME)
		.from(SERVICE_TECHNICIAN_GROUP).where(SERVICE_TECHNICIAN_GROUP.GROUP_ID.eq(id))
		.fetchOneInto(ServiceTechnicianGroup.class);
	}

	/**
	   * 分页查询售后分组信息
	 * @param param
	 * @return
	 */
	public PageResult<ServiceTechnicianGroup> getPageList(TechnicianGroupPageListParam param){
		SelectConditionStep<?> selectFrom = db().select(SERVICE_TECHNICIAN_GROUP.GROUP_ID, SERVICE_TECHNICIAN_GROUP.GROUP_NAME, SERVICE_TECHNICIAN_GROUP.STORE_ID, SERVICE_TECHNICIAN_GROUP.CREATE_TIME)
            .from(SERVICE_TECHNICIAN_GROUP).where(SERVICE_TECHNICIAN_GROUP.STORE_ID.eq(param.getStoreId())).and(SERVICE_TECHNICIAN_GROUP.DEL_FLAG.eq(SHORT_ZERO));
		PageResult<ServiceTechnicianGroup> pageResult = getPageResult(selectFrom, ServiceTechnicianGroup.class);
		return pageResult;
	}


    /**
     * Gets group all list.技师分组下拉框
     *
     * @param storeId the store id
     * @return the group all list
     */
    public List<ServiceTechnicianGroup> getGroupAllList(Integer storeId) {
        return db().select(SERVICE_TECHNICIAN_GROUP.GROUP_ID, SERVICE_TECHNICIAN_GROUP.STORE_ID, SERVICE_TECHNICIAN_GROUP.GROUP_NAME)
            .from(SERVICE_TECHNICIAN_GROUP).where(SERVICE_TECHNICIAN_GROUP.STORE_ID.eq(storeId))
            .and(SERVICE_TECHNICIAN_GROUP.DEL_FLAG.eq(SHORT_ZERO))
            .fetchInto(ServiceTechnicianGroup.class);
    }

	public int insert(ServiceTechnicianGroupParam param) {
		int result = db().insertInto(SERVICE_TECHNICIAN_GROUP, SERVICE_TECHNICIAN_GROUP.STORE_ID, SERVICE_TECHNICIAN_GROUP.GROUP_NAME)
			.values(param.getStoreId(),param.getGroupName())
			.execute();
		return result;
	}

	public int update(ServiceTechnicianGroupParam param) {
		int result = db().update(SERVICE_TECHNICIAN_GROUP)
				.set(SERVICE_TECHNICIAN_GROUP.GROUP_NAME, param.getGroupName())
				.where(SERVICE_TECHNICIAN_GROUP.GROUP_ID.eq(param.getGroupId()))
				.and(SERVICE_TECHNICIAN_GROUP.STORE_ID.eq(param.getStoreId()))
				.and(SERVICE_TECHNICIAN_GROUP.DEL_FLAG.eq(NORMAL))
				.execute();
		return result;
	}

    public void delete(Integer groupId) {
        db().update(SERVICE_TECHNICIAN_GROUP)
				.set(SERVICE_TECHNICIAN_GROUP.DEL_FLAG, DISABLE)
				.where(SERVICE_TECHNICIAN_GROUP.GROUP_ID.eq(groupId))
				.and(SERVICE_TECHNICIAN_GROUP.DEL_FLAG.eq(NORMAL))
				.execute();
	}


}
