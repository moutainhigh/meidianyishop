package com.meidianyi.shop.service.saas.shop;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.main.tables.records.MpVersionRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpVersionIdVo;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpVersionListParam;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpVersionListVo;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpVersionParam;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpVersionVo;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.WxOpenMaCodeTemplate;
import org.jooq.Record;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.SelectOnConditionStep;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.main.tables.MpAuthShop.MP_AUTH_SHOP;
import static com.meidianyi.shop.db.main.tables.MpVersion.MP_VERSION;

/**
 *
 * @author lixinguo
 *
 */
@Service
public class MpVersionService extends MainBaseService {

	public static final Byte PACK_VERSION_NORMAL = 1;
	public static final Byte PACK_VERSION_PLUGIN = 2;
	public static final Byte PACK_VERSION_NORMAL_LIVE = 3;
	public static final Byte PACK_VERSION_PLUGIN_LIVE = 4;
	public static final Byte NOT_IN_USE = 0;
	public static final Byte IN_USE = 1;

	/**
	 * 同步版本列表
	 *
	 * @return 返回模板的数量
	 * @throws WxErrorException
	 */
	public Integer synMpVersionList() throws WxErrorException {
		Integer lastTemplateId = 0;
		List<WxOpenMaCodeTemplate> list = open.getWxOpenComponentService().getTemplateList();
		List<Integer> wxOpenList=new ArrayList<Integer>(list.size());
		for (WxOpenMaCodeTemplate template : list) {
			MpVersionRecord record = db().newRecord(MP_VERSION);
			record.setCreateTime(new Timestamp(template.getCreateTime()*1000));
			record.setUserVersion(template.getUserVersion());
			record.setUserDesc(template.getUserDesc());
			record.setTemplateId(template.getTemplateId().intValue());
			MpVersionRecord row = getRow(template.getTemplateId().intValue());
			if(row==null) {
				db().executeInsert(record);
			}else {
				db().executeUpdate(record);
			}
			wxOpenList.add(record.getTemplateId());
			lastTemplateId = template.getTemplateId().intValue();
		}
		judgeDelFlag(wxOpenList);
		Integer useTemplateId = this.getCurrentUseTemplateId(null, PACK_VERSION_NORMAL);
		if (useTemplateId == 0 && lastTemplateId > 0) {
			// 如果没有当前的模板ID，最后的模板ID设置为当前使用模板ID
			this.setCurrentUseTemplateId(lastTemplateId);
		}
		return list.size();
	}

	/**
	 * 得到最新版本信息
	 *
	 * @return
	 */
	public MpVersionRecord getLastVersion() {
		return db().selectFrom(MP_VERSION).orderBy(MP_VERSION.TEMPLATE_ID.desc()).limit(1).fetchAny();
	}

	/**
	 * 得到最新版本信息，分为：1 正常版本 2 好物推荐版本 3 直播普通 4 直播好物
	 *
	 * @param appId          小程序appId，不为空时，查询小程序的使用版本
	 * @param packageVersion 插件版本标识：1 正常版本 2 好物推荐版本 3 直播普通 4 直播好物
	 * @return
	 */
	public MpVersionRecord getCurrentUseVersion(String appId, Byte packageVersion) {
		if (appId != null) {
			packageVersion = saas.shop.mp.getMpPackageVersion(appId);
		}
		MpVersionRecord record = db().fetchAny(MP_VERSION,
				MP_VERSION.CURRENT_IN_USE.eq((byte) 1).and(MP_VERSION.PACKAGE_VERSION.eq(packageVersion)));
		if (record == null && packageVersion != (byte) 1) {
			record = db().fetchAny(MP_VERSION,
					MP_VERSION.CURRENT_IN_USE.eq((byte) 1).and(MP_VERSION.PACKAGE_VERSION.eq((byte) 1)));
		}
		return record;
	}

	/**
	 * 得到当前使用的模板Id
	 *
	 * @param appId
	 * @param packageVersion
	 * @return
	 */
	public Integer getCurrentUseTemplateId(String appId, Byte packageVersion) {
		MpVersionRecord record = getCurrentUseVersion(appId, packageVersion);
		return record != null ? record.getTemplateId() : 0;
	}

	/**
	 * 得到当前使用的模板Id
	 *
	 * @param appId
	 * @return
	 */
	public Integer getCurrentUseTemplateId(String appId) {
		return getCurrentUseTemplateId(appId, PACK_VERSION_NORMAL);
	}

	/**
	 * 得到当前使用的模板Id
	 *
	 * @return
	 */
	public Integer getCurrentUseTemplateId() {
		return getCurrentUseTemplateId(null, PACK_VERSION_NORMAL);
	}

	/**
	 * 设置当前可用版本
	 *
	 * @param templateId
	 * @return
	 */
	public void setCurrentUseTemplateId(Integer templateId) {
		MpVersionRecord version = getRow(templateId);
		if (version != null) {
			db().update(MP_VERSION).set(MP_VERSION.CURRENT_IN_USE, (byte) 0).where(MP_VERSION.CURRENT_IN_USE
					.eq((byte) 1).and(MP_VERSION.PACKAGE_VERSION.eq(version.getPackageVersion()))).execute();

			db().update(MP_VERSION).set(MP_VERSION.CURRENT_IN_USE, (byte) 1)
					.where(MP_VERSION.TEMPLATE_ID.eq(templateId)).execute();
		}
	}

	/**
	 * 得到小程序模板版本列表
	 *
	 * @return
	 */
	public Result<MpVersionRecord> getAll() {
		return db().selectFrom(MP_VERSION).orderBy(MP_VERSION.TEMPLATE_ID.desc()).fetch();
	}

	/**
	 * 得到模板ID的版本记录
	 *
	 * @param templateId
	 * @return
	 */
	public MpVersionRecord getRow(Integer templateId) {
		return db().fetchAny(MP_VERSION, MP_VERSION.TEMPLATE_ID.eq(templateId));
	}

	/**
	 * 查询版本分页
	 *
	 * @param param
	 * @return
	 */
	public PageResult<MpVersionVo> getPageList(MpVersionListParam param) {
		SelectWhereStep<Record> select = db().select().from(MP_VERSION);
		select.orderBy(MP_VERSION.TEMPLATE_ID.desc());
		return this.getPageResult(select, param.getCurrentPage(),param.getPageRows(), MpVersionVo.class);
	}

    /**
     *  获取小程序版本名称列表值
     * @return 名称列表值
     */
	public List<MpVersionIdVo> getMpUserVersionList(){
        List<MpVersionIdVo> list = db().selectDistinct(MP_VERSION.USER_VERSION,MP_VERSION.TEMPLATE_ID)
            .from(MP_VERSION).orderBy(MP_VERSION.USER_VERSION.desc()).fetch().into(MpVersionIdVo.class);
        return list;
    }

	/**
	 * 得到小程序模板版本列表
	 *
	 * @return
	 */
	public Result<MpVersionRecord> getAllByDelFlag() {
		return db().selectFrom(MP_VERSION).where(MP_VERSION.DEL_FLAG.eq((byte) 0)).orderBy(MP_VERSION.TEMPLATE_ID.desc()).fetch();
	}

	/**
	 * 对删除的模板做判断
	 * @param wxOpenList
	 */
	public void judgeDelFlag(List<Integer> wxOpenList) {
		Result<MpVersionRecord> all = getAllByDelFlag();
		List<Integer> wxOldList=new ArrayList<Integer>(all.size());
		for(MpVersionRecord mpVersionRecord:all) {
			wxOldList.add(mpVersionRecord.get(MP_VERSION.TEMPLATE_ID));
		}
		boolean removeAll = wxOldList.removeAll(wxOpenList);
		if(removeAll&&(wxOldList.size()>0)) {
			db().update(MP_VERSION).set( MP_VERSION.DEL_FLAG,(byte) 1).where(MP_VERSION.TEMPLATE_ID.in(wxOldList)).execute();
		}
	}

	/**
	 * 更新当前包版本
	 * @param templateId
	 * @param packVersion
	 * @return
	 */
	public Integer updatePackVersion(Integer templateId,Byte packVersion) {
		return db().update(MP_VERSION).set(MP_VERSION.PACKAGE_VERSION,packVersion).where(MP_VERSION.TEMPLATE_ID.eq(templateId)).execute();
	}


	public PageResult<MpVersionListVo> getMpStat(MpVersionParam mVersionParam) {

		// MpVersionListVo 返回
		SelectOnConditionStep<Record7<Integer, String, Byte, Byte, Byte, Byte, Integer>> select = db()
				.select(MP_VERSION.TEMPLATE_ID, MP_VERSION.USER_VERSION, MP_AUTH_SHOP.IS_AUTH_OK, MP_AUTH_SHOP.OPEN_PAY,
						MP_AUTH_SHOP.AUDIT_STATE, MP_AUTH_SHOP.PUBLISH_STATE,
						DSL.count(MP_VERSION.TEMPLATE_ID).as("number"))
				.from(MP_AUTH_SHOP).innerJoin(MP_VERSION).on(MP_AUTH_SHOP.BIND_TEMPLATE_ID.eq(MP_VERSION.TEMPLATE_ID));
		if (mVersionParam.getTemplateId() != null) {
			select.where(MP_VERSION.TEMPLATE_ID.eq(mVersionParam.getTemplateId()));
		}
		if (!StringUtils.isEmpty(mVersionParam.getIsAuthOk())) {
			select.where(MP_AUTH_SHOP.IS_AUTH_OK.eq(mVersionParam.getIsAuthOk()));
		}
		if (!StringUtils.isEmpty(mVersionParam.getOpenPay())) {
			select.where(MP_AUTH_SHOP.OPEN_PAY.eq(mVersionParam.getOpenPay()));
		}
		if (!StringUtils.isEmpty(mVersionParam.getAuditState())) {
			select.where(MP_AUTH_SHOP.AUDIT_STATE.eq(mVersionParam.getAuditState()));
		}
		if (!StringUtils.isEmpty(mVersionParam.getPublishState())) {
			select.where(MP_AUTH_SHOP.PUBLISH_STATE.eq(mVersionParam.getPublishState()));
		}
		select.groupBy(MP_VERSION.TEMPLATE_ID, MP_VERSION.USER_VERSION,MP_AUTH_SHOP.IS_AUTH_OK, MP_AUTH_SHOP.OPEN_PAY, MP_AUTH_SHOP.AUDIT_STATE,
				MP_AUTH_SHOP.PUBLISH_STATE);
		select.orderBy(MP_VERSION.TEMPLATE_ID.desc());
		return this.getPageResult(select, mVersionParam.getCurrentPage(), mVersionParam.getPageRows(),
				MpVersionListVo.class);
	}
}
