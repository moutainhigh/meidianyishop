package com.meidianyi.shop.service.saas.shop;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.main.Tables;
import com.meidianyi.shop.db.main.tables.records.AppAuthRecord;
import com.meidianyi.shop.db.main.tables.records.AppRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;

import org.apache.commons.lang3.RandomStringUtils;
import org.jooq.Record1;
import org.springframework.stereotype.Service;

import static com.meidianyi.shop.db.main.tables.AppAuth.APP_AUTH;

/**
 *
 * @author zhaojianqiang
 *
 * 2019年9月23日 下午6:59:58
 */
@Service
public class ShopAppService  extends MainBaseService {
	public AppAuthRecord getShopAppByErp(Integer shopId) {
		return db().selectFrom(APP_AUTH).where(APP_AUTH.SHOP_ID.eq(shopId)).and(APP_AUTH.APP_ID.eq("200000")).fetchAny();
	}
    /** 门店对接action 2：pos */
    public static final Byte ACTION_POS = 2;
    public static final Byte ACTION_ERR = 1;
    public static final Byte ACTION_CRM = 3;
    /** status默认值0 */
    public static final Byte STATUS_DEFAULT_VALUE = 0;
    /**
     * 判断当前门店是否对接pos
     * @param shopId 店铺id
     * @return status 0：禁用 1：启用
     */
    public Byte getShopAppByPos(Integer shopId){
        Byte status = db().select(Tables.APP_AUTH.STATUS)
            .from(Tables.APP_AUTH)
            .where(Tables.APP_AUTH.SHOP_ID.eq(shopId))
            .and(Tables.APP_AUTH.APP_ID.eq("200001"))
            .fetchOptionalInto(Byte.class)
            .orElse(STATUS_DEFAULT_VALUE);
        return status;
    }

    /**
     * 验证appId和appSecret是否和数据库预置的一样
     * @param appId
     * @param appSecret
     * @return null表示appId或appSecret内容错误
     */
    public AppRecord getAppInfo(String appId, String appSecret) {
     return   db().selectFrom(Tables.APP)
           .where(Tables.APP.APP_ID.eq(appId).and(Tables.APP.APP_SECRET.eq(appSecret)))
           .fetchAny();
    }

    /**
     * 获取app
     * @param appId appid
     * @return null or record
     */
    public AppRecord getAppRecordByAppId(String appId){
        return db().selectFrom(Tables.APP).where(Tables.APP.APP_ID.eq(appId)).fetchOne();
    }

    /**
     * 获取appAuth
     * @param id id
     * @return
     */
    public AppAuthRecord getAppAuthRecordById(Integer id, Integer shopId,Integer sysId){
        return db().selectFrom(APP_AUTH).where(APP_AUTH.ID.eq(id))
            .and(APP_AUTH.SHOP_ID.eq(shopId))
            .and(APP_AUTH.SYS_ID.eq(sysId))
            .fetchOne();
    }
    /**
     * 根据sessionKey获取AppAuth信息
     * @param sessionKey
     * @return null表示店铺无有效授权
     */
    public AppAuthRecord getAppAuthInfoBySessionKey(String sessionKey) {
     return    db().selectFrom(APP_AUTH)
            .where(APP_AUTH.SESSION_KEY.eq(sessionKey).and(APP_AUTH.STATUS.eq((byte) 1)))
            .fetchAny();
    }

    /**
     * 获取授权信息
     * @param sysId 商家id
     * @param shopId 店铺id
     * @param action 1erp 2pos 3crm
     * @return null 或者授权信息
     */
    public AppAuthRecord getAppAuthInfo(Integer sysId, Integer shopId, Byte action) {
        String appId = "";
        if (ACTION_POS.equals(action)) {
            appId = "200001";
        } else if (ACTION_ERR.equals(action)) {
            appId = "200000";
        } else {
            appId = "200002";
        }
        return db().selectFrom(APP_AUTH)
            .where(APP_AUTH.SHOP_ID.eq(shopId))
            .and(APP_AUTH.SYS_ID.eq(sysId))
            .and(APP_AUTH.APP_ID.eq(appId)).fetchOne();

    }

    /**
     * 新增授权信息
     * @param sysId
     * @param shopId
     * @param action
     * @return
     */
    public AppAuthRecord addAppAuthInfo(Integer sysId, Integer shopId, Byte action){
        AppAuthRecord appAuthRecord = db().newRecord(APP_AUTH);
        appAuthRecord.setSysId(sysId);
        appAuthRecord.setShopId(shopId);
        String appId = "";
        if (ACTION_POS.equals(action)) {
            appId = "200001";
        } else if (ACTION_ERR.equals(action)) {
            appId = "200000";
        } else {
            appId = "200002";
        }
        appAuthRecord.setAppId(appId);
        appAuthRecord.setStatus(BaseConstant.NO);
        appAuthRecord.setSessionKey(generateUniqueSessionKey(shopId));
        appAuthRecord.insert();
        appAuthRecord.refresh();
        return appAuthRecord;
    }

    /**
     * 生成唯一的sessionKey
     * @param shopId 店铺id
     * @return  生成唯一的sessionKey
     */
    public String generateUniqueSessionKey(Integer shopId){
        int len= 32-shopId.toString().length()-16;
        String sessionKey;
        while (true){
            sessionKey = "w" + DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE) +
                RandomStringUtils.randomAlphanumeric(len) +
                "s" + shopId;
            Record1<Integer> fetchOne = db().selectCount().from(APP_AUTH).where(APP_AUTH.SESSION_KEY.eq(sessionKey)).fetchOne();
            if (fetchOne!=null&&fetchOne.component1()==0) {
                break;
            }
        }
        return sessionKey;
    }


    public int updateAppAuthStatus(Integer id, Integer shopId,Integer sysId, Byte status) {
        return db().update(APP_AUTH).set(APP_AUTH.STATUS,status)
            .where(APP_AUTH.ID.eq(id))
            .and(APP_AUTH.SYS_ID.eq(sysId))
            .and(APP_AUTH.SHOP_ID.eq(shopId)).execute();
    }

    /**
     *  更新appAuth的appkey数据
     * @param id id
     * @param sysId  sysid
     * @param shopId shopId
     * @param appKey appkey
     * @param appSecret sppsecret
     * @return 1
     */
    public int updateAppAuthAppkey(Integer id, Integer sysId, Integer shopId, String appKey, String appSecret) {
        return 0;
    }

    public int switchProduct(Integer id, Integer sysId, Integer shopId, byte product) {
       return 0;
    }
}
