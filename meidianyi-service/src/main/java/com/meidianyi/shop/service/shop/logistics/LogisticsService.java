package com.meidianyi.shop.service.shop.logistics;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.trade.LogisticsAccountInfo;
import com.meidianyi.shop.service.wechat.api.impl.WxOpenMaServiceExtraImpl;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.result.WxOpenResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;

import static com.meidianyi.shop.service.pojo.shop.market.form.FormConstant.MAPPER;

/**
 * The type Logistics service.
 *
 * @author liufei
 * @date 20190920
 */
@Slf4j
@Service
public class LogisticsService extends ShopBaseService {
    private static final String DATA = "data";

    /**
     * 获取支持的快递公司列表
     *
     * @return 微信支持的快递公司列表 all delivery
     */
    public List<LogisticsParam> getAllDelivery() {
        WxOpenMaServiceExtraImpl maService = open.getMaExtService();
        String jsonResult = StringUtils.EMPTY;
        try {
            jsonResult = maService.getAllDelivery(getAppId());
        } catch (WxErrorException e) {
            log.debug("微信api调用：获取支持的快递公司列表失败：{}", e.getMessage());
            throw new BusinessException(JsonResultCode.CODE_WX_LOGISTICS_API_CALL_FAILED);
        }
        log.debug("微信api调用：获取支持的快递公司列表调用结果为：{}", jsonResult);
        List<LogisticsParam> deliveryList;
        try {
            JsonNode node = MAPPER.readTree(jsonResult);
            JsonNode dataNode = node.get(DATA);
            Assert.notNull(dataNode, "微信api调用失败，获取data节点数据失败！");
            deliveryList = MAPPER.readValue(dataNode.traverse(), new TypeReference<List<LogisticsParam>>() {
            });
        } catch (IOException e) {
            log.error("data节点数据[{}]反序列化失败：{}", jsonResult, e.getMessage());
            throw new BusinessException(JsonResultCode.CODE_JACKSON_DESERIALIZATION_FAILED);
        }
        return deliveryList;
    }

    /**
     * 绑定物流公司
     *
     * @param bindAccountParam 入参json字符串
     * @return 微信api调用结果封装实例 wx open result
     * @throws WxErrorException 微信api调用异常
     * @see com.meidianyi.shop.service.pojo.shop.config.trade.BindAccountParam
     */
    public WxOpenResult bindAccount(String bindAccountParam) throws WxErrorException {
        WxOpenMaServiceExtraImpl maService = open.getMaExtService();
        return maService.bindAccount(getAppId(), bindAccountParam);
    }

    /**
     * 拉取已绑定账号
     *
     * @return 已绑定账号列表
     * @throws WxErrorException the wx error exception
     * @see com.meidianyi.shop.service.pojo.shop.config.trade.LogisticsAccountInfo
     */
    public List<LogisticsAccountInfo> getAllAccount() {
        List<LogisticsAccountInfo> accountInfos;
        WxOpenMaServiceExtraImpl maService = open.getMaExtService();
        String jsonResult = StringUtils.EMPTY;
        try {
            jsonResult = maService.getAllAccount(getAppId());
        } catch (WxErrorException e) {
            log.debug("微信api调用：拉取已绑定账号调用结果失败：{}", e.getMessage());
            throw new BusinessException(JsonResultCode.CODE_WX_LOGISTICS_API_CALL_FAILED);
        }
        log.debug("微信api调用：拉取已绑定账号调用结果为：{}", jsonResult);
        if (StringUtils.isBlank(jsonResult)) {
            return null;
        }
        try {
            JsonNode rootNode = MAPPER.readTree(jsonResult);
            JsonNode listNode = rootNode.get("list");
            Assert.notNull(listNode, "list节点不存在！");
            accountInfos = MAPPER.readValue(listNode.traverse(), new TypeReference<List<LogisticsAccountInfo>>() {
            });
        } catch (IOException e) {
            log.error("jackson反序列化时读取生成对象实例时失败：{}", e.getMessage());
            throw new BusinessException(JsonResultCode.CODE_JACKSON_DESERIALIZATION_FAILED);
        }
        return accountInfos;
    }

    /**
     * 获取接口调用凭证appid
     */
    private String getAppId() {
        return saas.shop.mp.getAuthShopByShopId(getShopId()).getAppId();
    }
}
