package com.meidianyi.shop.service.saas.shop;

import static com.meidianyi.shop.db.main.Tables.MP_AUTH_SHOP;
import static com.meidianyi.shop.db.main.tables.MpOperateLog.MP_OPERATE_LOG;
import static com.meidianyi.shop.db.main.tables.MpVersion.MP_VERSION;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.main.tables.records.MpOperateLogRecord;
import com.meidianyi.shop.db.main.tables.records.MpVersionRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpOperateListParam;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpOperateVo;
import com.meidianyi.shop.service.wechat.bean.ma.WxContentTemplate;

/**
 * 
 * @author lixinguo
 *
 */
@Service
public class MpOperateLogService extends MainBaseService {

    /**
     * 更新小程序信息
     */
    public static final Byte OP_TYPE_UPDATE_MP=0;

	/**
	 * 设置服务器域名
	 */
	public static final Byte OP_TYPE_MODIFY_DOMAIN = 1;

	/**
	 * 上传代码
	 */
	public static final Byte OP_TYPE_UPLOAD_CODE = 2;

	/**
	 * 添加体验者
	 */
	public static final Byte OP_TYPE_ADD_TESTER = 3;

	/**
	 * 删除体验者
	 */
	public static final Byte OP_TYPE_DEL_TESTER = 4;

	/**
	 * 获取体验者二维码
	 */
	public static final Byte OP_TYPE_GET_TESTER_QR = 5;

	/**
	 * 获取可选类目
	 */
	public static final Byte OP_TYPE_GET_CATEGORY = 6;

	/**
	 * 获取页面配置
	 */
	public static final Byte OP_TYPE_GET_PAGE_CFG = 7;

	/**
	 * 提交审核
	 */
	public static final Byte OP_TYPE_SUBMIT_AUDIT = 8;

	/**
	 * 发布代码
	 */
	public static final Byte OP_TYPE_PUBLISH_CODE = 9;

	/**
	 * 审核成功通知
	 */
	public static final Byte OP_TYPE_AUDIT_SUCCESS = 10;

	/**
	 * 审核失败通知
	 */
	public static final Byte OP_TYPE_AUDIT_FAILED = 11;

	/**
	 * 授权通知
	 */
	public static final Byte OP_TYPE_AUTH_OK = 12;

	/**
	 * 取消授权通知
	 */
	public static final Byte OP_TYPE_CANCEL_AUTH = 13;
	
	/**
	 * 操作成功
	 */
	public static final Byte OP_STATE_SUCCESS = 1;
	
	/**
	 * 操作失败
	 */
	public static final Byte OP_STATE_FAILED = 2;
	
	
	private static final String LANGUAGE_TYPE_WX = "wxapp";

	/**
	 * 上传代码并提交审核   一键提交审核
	 */
	public static final Byte OP_TYPE_UPLOAD_AUDIT = 14;
	
	/**
	 * 设置支付方式
	 */
	public static final Byte OP_TYPE_SETTING_SUB_MERCHANT = 15;
	
	/**
	 * 刷新审核状态
	 */
	public static final Byte OP_TYPE_REFRESH_AUDIT_STATE = 16;

	/**
	 * 记录小程序相关Log
	 * 
	 * @param appId       小程序appId
	 * @param templateId  模板Id
	 * @param operateType 操作类型
	 * @param operateState 操作状态 1 成功 2 失败
	 * @param memo        记录失败原因
	 * @return
	 */
	public int log(String appId, Integer templateId, Byte operateType, Byte operateState, String memo) {
		return db()
				.insertInto(MP_OPERATE_LOG, MP_OPERATE_LOG.APP_ID, MP_OPERATE_LOG.TEMPLATE_ID,
						MP_OPERATE_LOG.OPERATE_TYPE, MP_OPERATE_LOG.OPERATE_STATE,MP_OPERATE_LOG.MEMO)
				.values(appId, templateId, operateType, operateState,memo).execute();
	}
	
	public void insertRecord(Integer templateId,Byte operateType,String appId,Byte operateState,Integer templateIds, String... datas) {
		//mp_operate_log
		MpOperateLogRecord mLogRecord=MP_OPERATE_LOG.newRecord();
		StringBuilder dataStr = new StringBuilder();
		Arrays.stream(datas).forEach((x) -> dataStr.append(x).append(","));
		mLogRecord.setAppId(appId);
		mLogRecord.setTemplateId(templateId);
		mLogRecord.setMemo(dataStr.toString());
		mLogRecord.setOperateState(operateState);
		mLogRecord.setOperateType(operateType);
		//国家化的模板ID和内容
		mLogRecord.setMemoId(templateIds.toString());
		mLogRecord.setMemoList(dataStr.toString());
		db().executeInsert(mLogRecord);
	}

    /**
     *  通过店铺id查询小程序操作日志
     * @param param 分页过滤参数
     * @param shopId 小程序所属的店铺id
     * @return 日志结果
     */
	public PageResult<MpOperateVo> logList(MpOperateListParam param,Integer shopId,String language) {
        MpAuthShopRecord authShopByShopId = saas().shop.mp.getAuthShopByShopId(shopId);
        param.setAppId(authShopByShopId.getAppId());
        return logList(param,language);
    }

    /**
     * 小程序版本日志查询
     * @param param 查询参数
     * @return 日志结果
     */
    public PageResult<MpOperateVo> logList(MpOperateListParam param,String language){

        Logger logger = logger();

        logger.debug(String.format("小程序版本操作日志入参：%s",param.toString()));

        SelectConditionStep<Record8<Integer,Timestamp,String,String,String,String,String,String>> where = db().select(MP_OPERATE_LOG.TEMPLATE_ID, MP_OPERATE_LOG.CREATE_TIME, MP_OPERATE_LOG.APP_ID,
            MP_AUTH_SHOP.NICK_NAME, MP_OPERATE_LOG.MEMO, MP_VERSION.USER_VERSION,MP_OPERATE_LOG.MEMO_ID,MP_OPERATE_LOG.MEMO_LIST)
            .from(MP_OPERATE_LOG).leftJoin(MP_AUTH_SHOP)
            .on(MP_OPERATE_LOG.APP_ID.eq(MP_AUTH_SHOP.APP_ID))
            .leftJoin(MP_VERSION)
            .on(MP_OPERATE_LOG.TEMPLATE_ID.eq(MP_VERSION.TEMPLATE_ID))
            .where(param.buildOption());

        where.orderBy(MP_OPERATE_LOG.CREATE_TIME.desc());

        PageResult<MpOperateVo> pageResult = getPageResult(where, param.getCurrentPage(), param.getPageRows(), MpOperateVo.class);

        return pojoCompareToInfo(pageResult, language);
    }
    
    
	private PageResult<MpOperateVo> pojoCompareToInfo(PageResult<MpOperateVo> pageResult, String language) {
		List<MpOperateVo> dataList = pageResult.getDataList();
		if(dataList.size()==0) {
			return pageResult;
		}
		for (MpOperateVo vo : dataList) {
			vo.setMemo(splicingAdminRecordForContent(vo.getMemoId(), vo.getMemoList(), language));
		}
		pageResult.setDataList(dataList);
		return pageResult;

	}
	
	private String splicingAdminRecordForContent(String templateIds, String datas, String language) {
		int memoId = Integer.parseInt(templateIds);
		String message = WxContentTemplate.WX_ERROE.getMessage();
		for (WxContentTemplate wxt : WxContentTemplate.values()) {
			if (wxt.getCode() == memoId) {
				message = wxt.getMessage();
				break;
			}
		}
		return Util.translateMessage(language, message, LANGUAGE_TYPE_WX, (Object[]) datas.split(","));
	}
	
	/**
	 * 获取上线日志
	 * @param appId
	 * @return
	 */
	public List<MpOperateVo> getOperateLog(String appId) {
		int code = WxContentTemplate.WX_PUBLISH_CODE_SUCCESS.getCode();
		Result<Record7<Integer, Timestamp, String, String, String, String, String>> fetch = db()
				.select(MP_OPERATE_LOG.TEMPLATE_ID, MP_OPERATE_LOG.CREATE_TIME, MP_OPERATE_LOG.APP_ID,
						MP_OPERATE_LOG.MEMO, MP_VERSION.USER_VERSION, MP_OPERATE_LOG.MEMO_ID, MP_OPERATE_LOG.MEMO_LIST)
				.from(MP_OPERATE_LOG, MP_VERSION)
				.where(MP_OPERATE_LOG.TEMPLATE_ID.eq(MP_VERSION.TEMPLATE_ID)
						.and(MP_OPERATE_LOG.APP_ID.eq(appId).and(MP_OPERATE_LOG.MEMO_ID.eq(String.valueOf(code)))))
				.orderBy(MP_OPERATE_LOG.CREATE_TIME.asc()).fetch();
		if(null==fetch) {
			return null;
		}else {
			return fetch.into(MpOperateVo.class);
		}
		
	}
	
	/**
	 * 获得最后一次审核通过的版本
	 * @param appId
	 * @return
	 */
	public Byte getLastAuditSuccessPackage(String appId) {
		MpOperateLogRecord operateLog = db().selectFrom(MP_OPERATE_LOG)
				.where(MP_OPERATE_LOG.OPERATE_TYPE.eq(OP_TYPE_AUDIT_SUCCESS).and(MP_OPERATE_LOG.APP_ID.eq(appId)))
				.orderBy(MP_OPERATE_LOG.OPERATE_ID.desc()).fetchAny();
		if(operateLog!=null&&operateLog.getTemplateId()!=null) {
			MpVersionRecord template = db().selectFrom(MP_VERSION).where(MP_VERSION.TEMPLATE_ID.eq(operateLog.getTemplateId())).fetchAny();
			if(template==null) {
				return (byte)1;
			}
			return template.getPackageVersion();
		}
		return (byte)1;
	}
}
