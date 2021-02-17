package com.meidianyi.shop.service.shop.store.postsale;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.ServiceScheduleRecord;
import com.meidianyi.shop.db.shop.tables.records.ServiceTechnicianRecord;
import com.meidianyi.shop.db.shop.tables.records.ServiceTechnicianScheduleRecord;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.store.technician.*;
import com.meidianyi.shop.service.shop.store.schedule.TechnicianScheduleService;
import com.meidianyi.shop.service.shop.store.service.StoreServiceService;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.SelectConditionStep;
import org.jooq.SelectWhereStep;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.*;
import static com.meidianyi.shop.db.shop.tables.StoreService.STORE_SERVICE;
import static com.meidianyi.shop.service.shop.store.service.ServiceOrderService.DATE_TIME_FORMATTER;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;

/**
 * @author 黄荣刚
 * @date 2019年7月15日
 *
 */
@Service

public class ServiceTechnicianService extends ShopBaseService {

	public final static Byte SERVICE_TYPE_ALL=0;
	public final static Byte SERVICE_TYPE_PART=1;

	@Autowired public ServiceTechnicianGroupService groupService;
	@Autowired public TechnicianScheduleService scheduleService;
    @Autowired
    public StoreServiceService storeService;

	/**
	 * 根据ID查数据库一条记录
	 * @param id
	 * @return
	 */
	public ServiceTechnicianRecord selectRecord(Integer id) {
		if(id == null) {
			return null;
		}
		ServiceTechnicianRecord record = db()
						.selectFrom(SERVICE_TECHNICIAN)
						.where(SERVICE_TECHNICIAN.ID.eq(id))
						.and(SERVICE_TECHNICIAN.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
						.fetchOne();
		return record;
	}
	/**
	 * 根据ID查一条售后的信息
	 * @param id
	 * @return
	 */
	public ServiceTechnicianPojo select(Integer id) {
		ServiceTechnicianRecord technicianRecord = selectRecord(id);
		ServiceTechnicianPojo serviceTechnician = convert2ServiceTechnicianPojo(technicianRecord);
		return serviceTechnician;
	}
	/**
	 * 分页查询
	 * @param param
	 * @return
	 */
	public PageResult<ServiceTechnicianPojo> getPageList(ServiceTechnicianPageListParam param){
		SelectWhereStep<?> selectFrom = db().selectFrom(SERVICE_TECHNICIAN);
		SelectConditionStep<?> select = buildOptions(selectFrom,param);
		PageResult<ServiceTechnicianRecord> recordList = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(), ServiceTechnicianRecord.class);
		List<ServiceTechnicianRecord> dataList = recordList.getDataList();
		List<ServiceTechnicianPojo> pojoList = new ArrayList<ServiceTechnicianPojo>();
		if(dataList != null) {
			for (ServiceTechnicianRecord record : dataList) {
				ServiceTechnicianPojo pojo = convert2ServiceTechnicianPojo(record);
				pojoList.add(pojo);
			}
		}
		PageResult<ServiceTechnicianPojo> result = new PageResult<ServiceTechnicianPojo>();
		result.dataList = pojoList;
		result.page = recordList.page;
		return result;
	}
	/**
	 * 添加售后
	 * @param technicianParam
	 * @return
	 */
	public int insert(ServiceTechnicianParam technicianParam) {
		if(technicianParam == null) {
			return 0;
		}
        if (db().fetchExists(SERVICE_TECHNICIAN, SERVICE_TECHNICIAN.TECHNICIAN_NAME.eq(technicianParam.getTechnicianName())
            .and(SERVICE_TECHNICIAN.TECHNICIAN_MOBILE.eq(technicianParam.getTechnicianMobile())))) {
            throw new BusinessException(JsonResultCode.CODE_DATA_ALREADY_EXIST, "技师 " + technicianParam.getTechnicianName() + "-" + technicianParam.getTechnicianMobile());
        }
		String serviceList = null;
		if(SERVICE_TYPE_PART.equals(technicianParam.getServiceType())) {
			serviceList = Util.toJson(technicianParam.getServiceList());
		}
        ServiceTechnicianRecord record = new ServiceTechnicianRecord();
        FieldsUtil.assignNotNull(technicianParam, record);
        record.setServiceList(serviceList != null ? serviceList : "[]");
        return db().executeInsert(record);
	}
	/**
	 * 编辑售后
	 * @param param
	 * @return
	 */
	public int update(ServiceTechnicianParam param) {
		if(param == null) {
			return 0;
		}
		String serviceList = null;
		if(SERVICE_TYPE_PART.equals(param.getServiceType())) {
			serviceList = Util.toJson(param.getServiceList());
		}
		int result = db().update(SERVICE_TECHNICIAN)
			.set(SERVICE_TECHNICIAN.TECHNICIAN_NAME, param.getTechnicianName())
			.set(SERVICE_TECHNICIAN.TECHNICIAN_MOBILE, param.getTechnicianMobile())
			.set(SERVICE_TECHNICIAN.BG_IMG_PATH,param.getBgImgPath())
			.set(SERVICE_TECHNICIAN.TECHNICIAN_INTRODUCE,param.getTechnicianIntroduce())
			.set(SERVICE_TECHNICIAN.GROUP_ID,param.getGroupId())
			.set(SERVICE_TECHNICIAN.SERVICE_TYPE,param.getServiceType())
			.set(SERVICE_TECHNICIAN.SERVICE_LIST,serviceList)
			.set(SERVICE_TECHNICIAN.REMARKS,param.getRemarks())
			.where(SERVICE_TECHNICIAN.ID.eq(param.getId()))
			.execute();
		return result;
	}
	/**
	 * 删除售后
	 * @param id
	 * @return
	 */
	public int delete(Integer id) {
		if(id == null) {
			return 0;
		}
		int result = db().update(SERVICE_TECHNICIAN)
				.set(SERVICE_TECHNICIAN.DEL_FLAG, DelFlag.DISABLE_VALUE)
				.where(SERVICE_TECHNICIAN.ID.eq(id))
				.execute();
		return result;
	}

    /**
	 * @param selectFrom
	 * @param param
     * @return
	 */
	private SelectConditionStep<?> buildOptions(SelectWhereStep<?> selectFrom, ServiceTechnicianPageListParam param) {
		SelectConditionStep<?> where = selectFrom.where(SERVICE_TECHNICIAN.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
											.and(SERVICE_TECHNICIAN.STORE_ID.eq(param.getStoreId()));
		if(!StringUtils.isBlank(param.getTechnicianName())) {
			where = where.and(SERVICE_TECHNICIAN.TECHNICIAN_NAME.like(this.likeValue(param.getTechnicianName())));
		}
		if(!StringUtils.isBlank(param.getTechnicianMobile())) {
			where = where.and(SERVICE_TECHNICIAN.TECHNICIAN_MOBILE.like(this.likeValue(param.getTechnicianMobile())));
		}
		if(param.getGroupId() != null) {
			where = where.and(SERVICE_TECHNICIAN.GROUP_ID.eq(param.getGroupId()));
		}
		return where;
	}

	/**
     * @param record
	 * @return
	 */
	private ServiceTechnicianPojo convert2ServiceTechnicianPojo(ServiceTechnicianRecord record) {
		if(record == null) {
			return null;
		}
		ServiceTechnicianPojo pojo = new ServiceTechnicianPojo(record.getId(),record.getStoreId(),
				record.getTechnicianName(),record.getTechnicianMobile(),
				record.getBgImgPath(),record.getTechnicianIntroduce(),
				null,
				record.getServiceType(),null,
				record.getRemarks(),record.getCreateTime(),
				record.getUpdateTime());
		ServiceTechnicianGroup group = groupService.select(record.getGroupId());
		pojo.setSeviceGroup(group);

        if(SERVICE_TYPE_PART.equals(record.getServiceType())) {
			List<Integer> serviceId = Util.parseJson(record.getServiceList(), new TypeReference<List<Integer>>() {});
			pojo.setServiceList(selectServiceListByIdList(serviceId));
		}else {
			pojo.setServiceList(selectAllServiceByStoreId(record.getStoreId()));
		}

        return pojo;
	}

	/**
	 * @param serviceId
	 * @return
	 */
	private List<TechnicianService> selectServiceListByIdList(List<Integer> serviceId) {
		List<TechnicianService> list = db().selectFrom(STORE_SERVICE)
							.where(STORE_SERVICE.ID.in(serviceId))
							.and(STORE_SERVICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
							.fetchInto(TechnicianService.class);
		return list;
	}

	/**
	 * @return
	 */
	private List<TechnicianService> selectAllServiceByStoreId(Integer storeId) {
		if(storeId == null) {
			return null;
		}
		List<TechnicianService> list = db().selectFrom(STORE_SERVICE)
				.where(STORE_SERVICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
				.and(STORE_SERVICE.STORE_ID.eq(storeId))
				.fetchInto(TechnicianService.class);
		return list;
	}

    /**
     * Gets technician list.获取给定日期可服务技师信息列表
     *
     * @param storeId   the store id
     * @param startDate the date
     * @return the technician list
     */
    public List<TechnicianInfo> getTechnicianList(Integer storeId, LocalDate startDate, LocalDate endDate) {
        return db().select(
            SERVICE_TECHNICIAN.ID,
            SERVICE_TECHNICIAN.STORE_ID,
            SERVICE_TECHNICIAN.TECHNICIAN_NAME,
            SERVICE_TECHNICIAN.TECHNICIAN_MOBILE,
            SERVICE_TECHNICIAN.BG_IMG_PATH,
            SERVICE_TECHNICIAN.TECHNICIAN_INTRODUCE,
            SERVICE_TECHNICIAN.GROUP_ID,
            SERVICE_TECHNICIAN.SERVICE_TYPE,
            SERVICE_TECHNICIAN.SERVICE_LIST,
            SERVICE_TECHNICIAN.REMARKS,
            SERVICE_TECHNICIAN_SCHEDULE.WORK_DATE,
            SERVICE_TECHNICIAN_SCHEDULE.SCHEDULE_ID,
            SERVICE_SCHEDULE.SCHEDULE_NAME,
            SERVICE_SCHEDULE.BEGCREATE_TIME,
            SERVICE_SCHEDULE.END_TIME
        ).from(SERVICE_TECHNICIAN)
            .leftJoin(SERVICE_TECHNICIAN_SCHEDULE).on(SERVICE_TECHNICIAN.ID.eq(SERVICE_TECHNICIAN_SCHEDULE.TECHNICIAN_ID))
            .leftJoin(SERVICE_SCHEDULE).on(SERVICE_TECHNICIAN_SCHEDULE.SCHEDULE_ID.eq(SERVICE_SCHEDULE.SCHEDULE_ID))
            .where(SERVICE_TECHNICIAN.STORE_ID.eq(storeId))
            .and(SERVICE_TECHNICIAN_SCHEDULE.WORK_DATE.ge(startDate.format(DATE_TIME_FORMATTER)))
            .and(SERVICE_TECHNICIAN_SCHEDULE.WORK_DATE.le(endDate.format(DATE_TIME_FORMATTER)))
            .and(SERVICE_TECHNICIAN.DEL_FLAG.eq(BYTE_ZERO))
            .fetchInto(TechnicianInfo.class);
    }

    /**
     * Gets tech by store service.根据门店，服务查询可用技师列表
     *
     * @param param the param
     * @return the tech by store service
     */
    public List<TechnicianInfo> getTechByStoreService(TechnicianParam param) {
        // 该服务类型为无技师直接返回空列表
        if (BYTE_ZERO.equals(storeService.getSingleField(param.getServiceId(), STORE_SERVICE.SERVICE_TYPE))) {
            return Collections.emptyList();
        }
        List<TechnicianInfo> list = db().selectFrom(SERVICE_TECHNICIAN)
            .where(SERVICE_TECHNICIAN.STORE_ID.eq(param.getStoreId()))
            .and(SERVICE_TECHNICIAN.DEL_FLAG.eq(BYTE_ZERO))
            .fetchInto(TechnicianInfo.class);
        List<TechnicianInfo> result = list.stream().filter((e) -> BYTE_ZERO.equals(e.getServiceType())).collect(Collectors.toList());
        result.addAll(list.stream().filter((e) -> BYTE_ONE.equals(e.getServiceType()))
            .filter((e) -> !StringUtils.isBlank(e.getServiceList()))
            .filter((e) -> {
                List<Integer> temp = Util.json2Object(e.getServiceList(), new TypeReference<List<Integer>>() {
                }, false);
                if (CollectionUtils.isEmpty(temp)) {
                    return false;
                }
                return temp.contains(param.getServiceId());
            }).collect(Collectors.toList()));
        return result;
    }

    /**
     * 校验技师在指定时间段是否有排班
     * @param technicianId
     * @param serviceDate
     * @param servicePeriod
     * @return
     */
    public boolean isTechnicianEnable(int technicianId, String serviceDate,String servicePeriod){
        List<ServiceTechnicianScheduleRecord> list =
            db().selectFrom(SERVICE_TECHNICIAN_SCHEDULE).where(SERVICE_TECHNICIAN_SCHEDULE.TECHNICIAN_ID.eq(technicianId)).
            and(SERVICE_TECHNICIAN_SCHEDULE.WORK_DATE.eq(serviceDate)).fetch();
        if(!list.isEmpty()){
            for(ServiceTechnicianScheduleRecord t:list){
                ServiceScheduleRecord serviceScheduleRecord = db().fetchAny(SERVICE_SCHEDULE,SERVICE_SCHEDULE.SCHEDULE_ID.eq(t.getScheduleId()));
                if(serviceScheduleRecord != null){
                    Timestamp time = DateUtils.convertToTimestamp(serviceDate + " " + servicePeriod);
                    if(DateUtils.convertToTimestamp(serviceDate + " " + serviceScheduleRecord.getBegcreateTime() + ":00").before(time) && DateUtils.convertToTimestamp(serviceDate + " " + serviceScheduleRecord.getEndTime() + ":00").after(time)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
