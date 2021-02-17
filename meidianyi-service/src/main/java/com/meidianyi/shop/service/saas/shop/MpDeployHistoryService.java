package com.meidianyi.shop.service.saas.shop;

import static com.meidianyi.shop.db.main.tables.MpDeployHistory.MP_DEPLOY_HISTORY;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.MpDeployHistoryRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;

/**
 * @author lixinguo
 */
@Service
public class MpDeployHistoryService extends MainBaseService {
		

	public MpDeployHistoryRecord getDeployInfo(String appId, Integer templateId) {
		return db().selectFrom(MP_DEPLOY_HISTORY)
				.where(MP_DEPLOY_HISTORY.BIND_TEMPLATE_ID.eq(templateId).and(MP_DEPLOY_HISTORY.APP_ID.eq(appId)))
				.fetchAny();
	
	}

	public int addRow(String appId, Integer templateId) {
		return db()
				.insertInto(MP_DEPLOY_HISTORY, MP_DEPLOY_HISTORY.BIND_TEMPLATE_ID, MP_DEPLOY_HISTORY.APP_ID,
						MP_DEPLOY_HISTORY.DEPLOY_TIME)
				.values(templateId, appId, new Timestamp(System.currentTimeMillis())).execute();
	}

	public int update(String appId, Integer templateId,List<String> list) {
		MpDeployHistoryRecord record=MP_DEPLOY_HISTORY.newRecord();
		record.setAppId(appId);
		record.setBindTemplateId(templateId);
		record.setPageCfg(Util.toJson(list));
		return db().executeUpdate(record);
	}

}
