package com.meidianyi.shop.service.shop.config;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.main.tables.records.AppAuthRecord;
import com.meidianyi.shop.db.main.tables.records.AppRecord;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.pojo.shop.config.trade.third.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static com.meidianyi.shop.service.pojo.shop.config.trade.third.ThirdErpPushParam.*;

/**
 * 第三方对接配置
 * ERP服务:200000
 * POS服务:200001
 * CRM服务:200002
 * @author 孔德成
 * @date 2020/5/18
 */
@Service
public class ThirdAuthConfigService extends  BaseShopConfigService {

    /**
     * 同城配送订单已收货后推送 1 开启 0关闭
     */
    final public static String K_CITY_ORDER_PUSH = "city_order_push";
    /**
     * 自提订单核销后推送 1开启 0关闭
     */
    final public static String K_VERIFY_ORDER = "verify_order";
    @Autowired
    private ShopReturnConfigService shopReturnConfigService;


    public  Byte  getCityOrderPush(){
        return this.get(K_CITY_ORDER_PUSH, Byte.class, (byte)0);
    }

    public  Byte  getVerifyOrder(){
        return this.get(K_VERIFY_ORDER, Byte.class, (byte)0);
    }

    public void setCityOrderPush(@NotNull @Min(0) @Max(1) Byte action) {
        this.set(K_CITY_ORDER_PUSH,action.toString());
    }

    public void verifyOrder(@NotNull @Min(0) @Max(1) Byte action) {
        this.set(K_VERIFY_ORDER,action.toString());
    }

    public void setPush(ThirdErpPushParam param) {
        switch (param.getType()){
            case K_CITY_ORDER_PUSH:
                setCityOrderPush(param.getStatus());
                break;
            case K_VERIFY_ORDER:
                verifyOrder(param.getStatus());
                break;
            default:
                throw new  BusinessException(JsonResultCode.CODE_CARD_RECEIVE_NOCODE);
        }
    }

    public ThirdInfoVo getThirdAuthInfo(ThirdInfoParam param) {
        //初始化信息
        AppAuthRecord appAuthRecord = this.authInit(param.getAction());
        AppRecord appInfo = this.getAppInfo(param);
        AppAuthBo appAuthBo = appAuthRecord.into(AppAuthBo.class);
        AppBo appBo = appInfo.into(AppBo.class);

        ThirdInfoVo  vo =new ThirdInfoVo();
        vo.setAppAuthBo(appAuthBo);
        vo.setAppBo(appBo);
        vo.setCityOrderPush(getCityOrderPush());
        vo.setVerifyOrder(getVerifyOrder());
        return vo;
    }

    private AppRecord getAppInfo(ThirdInfoParam param) {
        String appId;
        switch (param.getAction()) {
            case 1:
                appId="200000";
                break;
            case 2:
                appId="200001";
                break;
            case 3:
                appId="200002";
                break;
            default:
                throw new BusinessException(JsonResultCode.CODE_CARD_RECEIVE_NOCODE);
        }
        return saas.shop.shopApp.getAppRecordByAppId(appId);
    }

    /**
     * 初始化授权
     * @param action
     * @return
     */
    private AppAuthRecord authInit(Byte action){
        if (action!=null){
            Integer shopId = getShopId();
            Integer sysId = getSysId();
            AppAuthRecord appAuthInfo = saas.shop.shopApp.getAppAuthInfo(sysId, shopId, action);
            if (appAuthInfo==null){
               return saas.shop.shopApp.addAppAuthInfo(sysId,shopId,action);
            }
            return appAuthInfo;
        }
        return null;
    }


    /**
     * 授权
     * @param param
     * @return
     */
    public int authorize(ThirdAuthorizeParam param) {
        int i = saas.shop.shopApp.updateAppAuthStatus(param.getId(),getShopId(),getSysId(),param.getStatus());
        if (i>0&& param.getAction().equals(CRM_ACTION)&& BaseConstant.YES.equals(param.getStatus())){
            logger().info("授权crm-同步");
            AppAuthRecord appAuthRecord = saas.shop.shopApp.getAppAuthInfo(getSysId(), getShopId(), param.getAction());
                //TODO
                /**
                 *   queue_job(function () use ($shopId){
                 *    shop($shopId)->serviceRequest->crmApi->startSyncAllData();
                 *      });
                 */
        }
        return i;
    }

    public int resetSessionKey(Integer id) {
        AppAuthRecord appAuthRecord = saas.shop.shopApp.getAppAuthRecordById(id, getShopId(), getSysId());
        if (appAuthRecord!=null){
            String s = saas.shop.shopApp.generateUniqueSessionKey(getShopId());
            appAuthRecord.setSessionKey(s);
            appAuthRecord.setUpdateTime(DateUtils.getLocalDateTime());
           return appAuthRecord.update();
        }
        return 0;
    }

    public int saveAppKey(ThirdAppKeyParam param) {
        int i=0;
        switch (param.getAction()){
            case REP_ACTION:
                logger().info("crm更新appkey");
                 i = saas.shop.shopApp.updateAppAuthAppkey(param.getId(), getSysId(), getShopId(), param.getAppKey(), param.getAppSecret());
                break;
            case POS_ACTION:
                logger().info("crm更新appkey");
                i=saas.shop.shopApp.updateAppAuthAppkey(param.getId(), getSysId(), getShopId(), param.getAppKey(), param.getAppSecret());
                break;
            case CRM_ACTION:
                logger().info("crm更新appkey");
                i=saas.shop.shopApp.updateAppAuthAppkey(param.getId(), getSysId(), getShopId(), param.getAppKey(), param.getAppSecret());
                break;
            default:
        }
        return i;
    }

    public int switchProduct(ThirdSwitchProductParam param) {
        return saas.shop.shopApp.switchProduct(param.getId(),getSysId(),getShopId(),param.getProduct());
    }

    /**
     * pos同步
     * @param param
     */
    public void posSyncStoreGoods(ThirdPosSyncParam param) {
        logger().info("pos同步数据{}",param.getAction());


    }
}
