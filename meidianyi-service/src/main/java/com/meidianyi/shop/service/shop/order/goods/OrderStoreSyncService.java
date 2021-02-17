package com.meidianyi.shop.service.shop.order.goods;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalGateConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestResult;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.order.goods.store.*;
import org.springframework.stereotype.Service;

/**
 * 订单-药房信息同步service
 * @author 李晓冰
 * @date 2020年08月27日
 */
@Service
public class OrderStoreSyncService extends ShopBaseService {

    /**
     * 推送订单信息至药房
     * @param param
     * @return true推送成功，推送失败
     */
    public boolean pushOrderInfoToStore(OrderStorePosBo param){
        String appId = ApiExternalGateConstant.APP_ID_STORE;
        Integer shopId = getShopId();
        String serviceName = ApiExternalRequestConstant.SERVICE_NAME_SYNC_ORDER_POS_INFO;
        ApiExternalRequestResult apiExternalRequestResult = saas().apiExternalRequestService.externalRequestGate(appId, shopId, serviceName, Util.toJson(param));
        if (ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())) {
            return true;
        } else {
            logger().error("推送订单消息至药房："+param.getShopSn()+"，错误："+apiExternalRequestResult.getMsg());
            return false;
        }
    }

    /**
     * 同步药房指定药品的价格和数量
     * @param param
     * @return
     */
    public StoreGoodsConfirmVo syncGoodsInfosFromStore(StoreGoodsNumConfirmParam param){
        String appId = ApiExternalGateConstant.APP_ID_STORE;
        Integer shopId = getShopId();
        String serviceName = ApiExternalRequestConstant.SERVICE_NAME_SYNC_GOODS_INFOS;
        ApiExternalRequestResult apiExternalRequestResult = saas().apiExternalRequestService.externalRequestGate(appId, shopId, serviceName, Util.toJson(param));
        if (!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())){
            logger().error("查询药房："+param.getShopSn()+"指定商品价格库存信息错误，请求信息："+param.toString()+",错误信息："+apiExternalRequestResult.getMsg());
            return null;
        }

        return Util.parseJson(apiExternalRequestResult.getData(), StoreGoodsConfirmVo.class);
    }

    public OrderStockEnoughQueryVo getStockEnoughShopList(OrderStockEnoughQueryParam param){
        String appId = ApiExternalGateConstant.APP_ID_STORE;
        Integer shopId = getShopId();
        String serviceName = ApiExternalRequestConstant.SERVICE_NAME_GET_STOCK_ENOUGH_SHOP_LIST;
        ApiExternalRequestResult apiExternalRequestResult = saas().apiExternalRequestService.externalRequestGate(appId, shopId, serviceName, Util.toJson(param));
        if (!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())){
            logger().error("请求信息："+param.toString()+",错误信息："+apiExternalRequestResult.getMsg());
            return null;
        }

        return Util.parseJson(apiExternalRequestResult.getData(), OrderStockEnoughQueryVo.class);
    }

    public boolean cancelOrder(OrderStoreCancelParam param){
        String appId = ApiExternalGateConstant.APP_ID_STORE;
        Integer shopId = getShopId();
        String serviceName = ApiExternalRequestConstant.SERVICE_NAME_CANCEL_ORDER;
        ApiExternalRequestResult apiExternalRequestResult = saas().apiExternalRequestService.externalRequestGate(appId, shopId, serviceName, Util.toJson(param));
        if (!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())) {
            logger().error("请求信息：" + param.toString() + ",错误信息：" + apiExternalRequestResult.getMsg());
            return false;
        } else {
            return true;
        }
    }
}
