package com.meidianyi.shop.service.shop.order;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalGateConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalGateParam;
import com.meidianyi.shop.common.pojo.saas.api.ApiJsonResult;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.order.api.ApiReturnParam;
import com.meidianyi.shop.service.pojo.shop.order.api.ApiShippingParam;
import com.meidianyi.shop.service.pojo.shop.order.api.ApiSyncOrderStatusParam;
import com.meidianyi.shop.service.shop.order.action.ReturnService;
import com.meidianyi.shop.service.shop.order.action.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 王帅
 */
@Service
public class OrderApiService extends ShopBaseService {

    @Autowired
    public ShipService shipService;
    @Autowired
    public ReturnService returnService;
    /**
     * 发货
     * @param gateParam
     * @return
     */
    public ApiJsonResult shipping(ApiExternalGateParam gateParam) {
        try {
            return shipService.shippingApi(Util.parseJson(gateParam.getContent(), ApiShippingParam.class));
        } catch (MpException e) {
            ApiJsonResult apiJsonResult = new ApiJsonResult();
            apiJsonResult.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            apiJsonResult.setMsg(e.getMessage());
            return apiJsonResult;
        }
    }

    /**
     * 退款/退货
     * @param param
     * @return
     */
    public ApiJsonResult returnOrder(ApiExternalGateParam param) {
        return returnService.returnOrderApi(Util.parseJson(param.getContent(), ApiReturnParam.class));
    }

    /**
     * 药房主动取消对应订单
     * @param param
     * @return
     */
    public ApiJsonResult storeCancelOrder(ApiSyncOrderStatusParam param){
        // TODO: 药房主动取消对应订单
        return null;
    }

}
