package com.meidianyi.shop.service.shop.operation;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.AuthConfig;
import com.meidianyi.shop.db.main.tables.records.ShopAccountRecord;
import com.meidianyi.shop.db.main.tables.records.ShopChildAccountRecord;
import com.meidianyi.shop.db.shop.tables.records.RecordAdminActionRecord;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.operation.RecordAdminActionInfo;
import com.meidianyi.shop.service.pojo.shop.operation.RecordAdminActionParam;
import com.meidianyi.shop.service.pojo.shop.operation.RecordAdminActionPojo;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.SelectWhereStep;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.RecordAdminAction.RECORD_ADMIN_ACTION;

/**
 * 操作记录的实现逻辑
 *
 * @author: 卢光耀
 * @date: 2019-07-12 10:21
 *
 */
@Service

public class RecordAdminActionService extends ShopBaseService {

	private static final String REDIS_PACKAGE = "record.user.";

	protected static final String REDIS_TIMEOUT = "auth.timeout";

	private static final String REQUEST_TYPE_SYSTEM = "system";

	private static final String REQUEST_TYPE_ACCOUNT = "account";

	private static final String REQUEST_TYPE_NONE = "none";

	private static final String LANGUAGE_TYPE_RECORD = "record";

	@Autowired
	protected JedisManager jedis;

	@Autowired
	protected AuthConfig authConfig;

	/**
	 *
	 * @param templateIds 模版id {@link RecordContentTemplate}
     *                    和{@link com.meidianyi.shop.service.pojo.shop.operation.RecordContentMessage}
	 * @param datas       模版数据
	 */
	public void insertRecord(List<Integer> templateIds, String... datas) {
		RecordAdminActionRecord record = this.getAdminRecord();
		StringBuilder templateIdStr = new StringBuilder();
		StringBuilder dataStr = new StringBuilder();
		templateIds.stream().forEach((x) -> templateIdStr.append(x).append(","));
		Arrays.stream(datas).forEach((x) -> dataStr.append(x).append(","));
		record.setTemplateId(templateIdStr.toString());
		record.setTemplateData(dataStr.toString());
		record.setActionType(Byte.valueOf(templateIdStr.toString().substring(0, 1)));
		record.insert();
	}

	public PageResult<RecordAdminActionInfo> getRecordPage(RecordAdminActionParam param, String language) {
		PageResult<RecordAdminActionPojo> pgPojo;
		SelectWhereStep<? extends Record> select = db()
				.select(RECORD_ADMIN_ACTION.USER_NAME, RECORD_ADMIN_ACTION.MOBILE,
						RECORD_ADMIN_ACTION.CREATE_TIME, RECORD_ADMIN_ACTION.ACTION_TYPE,
						RECORD_ADMIN_ACTION.TEMPLATE_ID, RECORD_ADMIN_ACTION.TEMPLATE_DATA,RECORD_ADMIN_ACTION.ACCOUNT_TYPE)
				.from(RECORD_ADMIN_ACTION);
		buildParams(select, param);
		select.orderBy(RECORD_ADMIN_ACTION.CREATE_TIME.desc());
		if (null != param.getCurrentPage()) {
			pgPojo = getPageResult(select, param.getCurrentPage(), param.getPageRows(), RecordAdminActionPojo.class);
		} else {
			pgPojo = getPageResult(select, param.getPageRows(), RecordAdminActionPojo.class);
		}
		return this.pojoCompareToInfo(pgPojo, language);
	}

	private PageResult<RecordAdminActionInfo> pojoCompareToInfo(PageResult<RecordAdminActionPojo> pgPojo,
			String language) {
		PageResult<RecordAdminActionInfo> pageInfo = new PageResult<>();
		BeanUtils.copyProperties(pgPojo, pageInfo);
		List<RecordAdminActionInfo> infos = new ArrayList<>();
		List<RecordAdminActionPojo> pojo = pgPojo.dataList;
		pojo.forEach((pj) -> {
			infos.add(RecordAdminActionInfo.builder()
					.createTime(pj.getCreateTime())
					.userName(pj.getUserName() + ":" + pj.getMobile())
					.actionTypeName(RecordAdminActionPojo.ActionType.getNameByCode((int) pj.getActionType()))
					.content(this.splicingAdminRecordForContent(pj.getTemplateId(), pj.getTemplateData(), language))
					.accountType(pj.getAccountType())
					.actionType(pj.getActionType())
					.build());
		});
		pageInfo.setDataList(infos);
		return pageInfo;

	}

	private void buildParams(SelectWhereStep<? extends Record> select, RecordAdminActionParam param) {
		if (StringUtils.isNotBlank(param.getUserName())) {
			select.where(RECORD_ADMIN_ACTION.USER_NAME.eq(param.getUserName()));
		}
		if (null != param.getEndCreateTime() && null != param.getStartCreateTime()) {
			select.where(RECORD_ADMIN_ACTION.CREATE_TIME.between(param.getStartCreateTime(), param.getEndCreateTime()));
		}
		if (null != param.getStartCreateTime()) {
			select.where(RECORD_ADMIN_ACTION.CREATE_TIME.greaterOrEqual(param.getStartCreateTime()));
		}
		if (null != param.getEndCreateTime()) {
			select.where(RECORD_ADMIN_ACTION.CREATE_TIME.lessOrEqual(param.getEndCreateTime()));
		}
		if (null!=param.getActionType()) {
			select.where(RECORD_ADMIN_ACTION.ACTION_TYPE.eq(param.getActionType()));
		}
	}

	private String splicingAdminRecordForContent(String templateIds, String datas, String language) {
		StringBuilder sb = new StringBuilder();

		List<String> templateMsgList = Arrays.stream(templateIds.split(","))
            .map(Integer::parseInt)
            .map(RecordContentTemplate::getMessageByCode)
            .collect(Collectors.toList());

		return Util.translateMessage(language, templateMsgList,
            LANGUAGE_TYPE_RECORD, (Object[]) datas.split(","));
	}

	private RecordAdminActionRecord getAdminRecord() {
		RecordAdminActionRecord record = db().newRecord(RECORD_ADMIN_ACTION);
		List<String> resultAccount;
		AdminTokenAuthInfo info = ShopBaseService.getCurrentAdminLoginUser();
		if (info != null) {
			Integer accountId = info.getSubAccountId();
			if (info.isSubLogin()) {
				record.setAccountId(accountId);
				resultAccount = this.getCommonNameAndMobile(accountId, REQUEST_TYPE_ACCOUNT);
			} else {
				resultAccount = this.getCommonNameAndMobile(info.getSysId(), REQUEST_TYPE_SYSTEM);
			}
			record.setSysId(info.getSysId());
		} else {
			Integer sysId = this.getSystemIdByNoRequest();
			resultAccount = this.getCommonNameAndMobile(sysId, REQUEST_TYPE_NONE);
			record.setSysId(sysId);
		}
		record.setUserName(resultAccount.get(0));
		record.setMobile(resultAccount.size()>1?resultAccount.get(1):"");
		return record;
	}

	private Integer getSystemIdByNoRequest() {
		return this.getSysId();
	}

	private String getNameAndMobileByNoRequest() {
		Integer sysId = this.getSysId();
		if (sysId != 0) {
			ShopAccountRecord account = saas.shop.account.getAccountInfoForId(sysId);
			return String.format("%s,%s", account.getAccountName(), account.getMobile());
		}
		return "";
	}

	private String getSystemNameAndMobile(Integer sysId, List<String> resultList) {
		ShopAccountRecord account = saas.shop.account.getAccountInfoForId(sysId);
		resultList.add(account.getUserName());
		resultList.add(account.getMobile());
		return String.format("%s,%s", account.getAccountName(), account.getMobile());
	}

	private List<String> getCommonNameAndMobile(Integer id, String type) {
		String key = REDIS_PACKAGE + id;
		Integer timeout = authConfig.getTimeout();
		List<String> resultList = new ArrayList<String>(2);
		String result = jedis.getValueAndSave(key, timeout, () -> {
			switch (type) {
                case REQUEST_TYPE_ACCOUNT:
                    return this.getAccountNameAndMobile(id, resultList);
                case REQUEST_TYPE_SYSTEM:
                    return this.getSystemNameAndMobile(id, resultList);
                case REQUEST_TYPE_NONE:
                    return this.getNameAndMobileByNoRequest();
                default:
                    return "";
            }
        });
        if (result == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(result.split(","));
    }

	private String getAccountNameAndMobile(Integer accountId, List<String> resultList) {
		ShopChildAccountRecord subAccount = saas.shop.subAccount.getSubAccountInfo(accountId);
		resultList.add(subAccount.getAccountName());
		resultList.add(subAccount.getMobile());
		return String.format("%s,%s", subAccount.getAccountName(),subAccount.getMobile());
	}

}
