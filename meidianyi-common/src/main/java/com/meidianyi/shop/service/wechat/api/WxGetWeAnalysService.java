package com.meidianyi.shop.service.wechat.api;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.google.gson.JsonObject;
import com.meidianyi.shop.service.pojo.shop.summary.portrait.MaPortraitResult;

import me.chanjar.weixin.common.error.WxErrorException;

/**
 * 获取小程序新增或活跃用户的画像分布数据
 * 因为自带的会转换数据类型，再转回去太麻烦
 *
 * @author zhaojianqiang
 * @time 上午9:23:21
 */
public interface WxGetWeAnalysService extends WxOpenMaMpHttpBase {
    /**
     * 获取小程序新增或活跃用户的画像分布数据
     */
    String GET_USER_PORTRAIT_URL = "https://api.weixin.qq.com/datacube/getweanalysisappiduserportrait";


    /**
     * 获取小程序新增或活跃用户的画像分布数据
     *
     * @param appId
     * @param beginDate
     * @param endDate
     * @return
     * @throws WxErrorException
     */
    default MaPortraitResult getUserPortrait(String appId, Date beginDate, Date endDate) throws WxErrorException {
        String json = post(appId, GET_USER_PORTRAIT_URL, toJson(beginDate, endDate));
        return MaPortraitResult.fromJson(json);
    }

    /**
     * to json
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    static String toJson(Date beginDate, Date endDate) {
        JsonObject param = new JsonObject();
        param.addProperty("begin_date", DateFormatUtils.format(beginDate, "yyyyMMdd"));
        param.addProperty("end_date", DateFormatUtils.format(endDate, "yyyyMMdd"));
        return param.toString();
    }

}
