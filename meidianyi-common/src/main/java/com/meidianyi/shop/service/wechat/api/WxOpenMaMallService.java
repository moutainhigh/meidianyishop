package com.meidianyi.shop.service.wechat.api;

import com.meidianyi.shop.service.wechat.bean.open.WxOpenGetResult;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.result.WxOpenResult;

/**
 * 微信好物圈的操作
 * @author zhaojianqiang
 *
 * 2019年11月12日 下午5:47:32
 */
public interface WxOpenMaMallService extends WxOpenMaMpHttpBase {
    /**
     * 导入订单的
     * {@value} https://api.weixin.qq.com/mall/importorder?action=add-order&is_history=0/1&access_token=ACCESS_TOKEN
     */
    static final String WX_COMMOND_IMPORTORDER_ADD = "https://api.weixin.qq.com/mall/importorder?action=add-order&is_history=0";
    /**
     * 导入订单测试地址
     */
    static final String WX_COMMOND_IMPORTORDER_ADD_TEST = "https://api.weixin.qq.com/mall/importorder?action=add-order&is_test=1";

    /**
     * 更新订单信息
     */
    static final String WX_COMMOND_IMPORTORDER_UPDATE = "https://api.weixin.qq.com/mall/importorder?action=update-order&is_history=0";
    
    /**
     * 导入收藏
     */
    static final String WX_ADDSHOPPINGLIST_ADD = "https://api.weixin.qq.com/mall/addshoppinglist";

    
    
    /**
     * 删除收藏
     */
    static final String WX_ADDSHOPPINGLIST_DEL = "https://api.weixin.qq.com/mall/deleteshoppinglist";
    
    
    /**
     * 更新或导入物品信息
     */
    static final String WX_IMPORTPRODUCT_UPDATE = "https://api.weixin.qq.com/mall/importproduct";

    /**
     * 导入订单
     *
     * @param appId https调用凭证
     *              {@value LOGISTICS_GET_ALL_ACCOUNT}
     * @param orderList
     * @return
     * @throws WxErrorException
     */
    default WxOpenResult importorderAdd(String appId,String orderList) throws WxErrorException {
    	String json = post(appId, WX_COMMOND_IMPORTORDER_ADD, orderList);
    	return WxOpenGetResult.fromJson(json);
    }

    
    /**
     * 导入订单
     *
     * @param appId https调用凭证
     *              {@value LOGISTICS_GET_ALL_DELIVERY}
     * @param orderList
     * @return
     * @throws WxErrorException
     */
    default WxOpenResult importorderUpdate(String appId,String orderList) throws WxErrorException {
    	String json = post(appId, WX_COMMOND_IMPORTORDER_UPDATE, orderList);
    	return WxOpenGetResult.fromJson(json);
    }

    
    /**
     * 导入收藏
     *
     * @param appId https调用凭证
     *              {@value LOGISTICS_GET_ALL_DELIVERY}
     * @param orderList
     * @return
     * @throws WxErrorException
     */
    default WxOpenResult addshoppinglistAdd(String appId,String orderList) throws WxErrorException {
    	String json = post(appId, WX_ADDSHOPPINGLIST_ADD, orderList);
    	return WxOpenGetResult.fromJson(json);
    }

    
    /**
     * 删除收藏
     *
     * @param appId https调用凭证
     *              {@value LOGISTICS_GET_ALL_DELIVERY}
     * @param orderList
     * @return
     * @throws WxErrorException
     */
    default WxOpenResult addshoppinglistDel(String appId,String orderList) throws WxErrorException {
    	String json = post(appId, WX_ADDSHOPPINGLIST_DEL, orderList);
    	return WxOpenGetResult.fromJson(json);
    }
    
    /**
     * 删除收藏
     *
     * @param appId https调用凭证
     *              {@value LOGISTICS_GET_ALL_DELIVERY}
     * @param orderList
     * @return
     * @throws WxErrorException
     */
    default WxOpenResult importProductUpdate(String appId,String orderList) throws WxErrorException {
    	String json = post(appId, WX_IMPORTPRODUCT_UPDATE, orderList);
    	return WxOpenGetResult.fromJson(json);
    }
}
