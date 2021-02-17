package com.meidianyi.shop.service.shop.user.message;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateConfig;
import com.meidianyi.shop.service.saas.shop.MpAuthShopService;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.api.WxMaTemplateService;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateData;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.binarywang.wx.miniapp.bean.template.WxMaTemplateAddResult;
import cn.binarywang.wx.miniapp.bean.template.WxMaTemplateListResult;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * @author lixinguo
 */
@Service
public class MaTemplateService extends ShopBaseService {

    @Autowired
    private MpAuthShopService mpAuthShopService;
    @Autowired
    private JedisManager jedis;
	
	public void sendTemplateMessage(WxMaTemplateMessage msg) throws WxErrorException {
        mpAuthShopService.getMaServiceByShopId(getShopId()).getMsgService().sendTemplateMsg(msg);
	}
    public String addTemplate(WxMaTemplateService templateService ,String templateId, List<Integer> keywordIds) throws WxErrorException {
        WxMaTemplateAddResult result = templateService.addTemplate(templateId, keywordIds);
        return result.getTemplateId();

    }
    public void sendMaTemplateMessage(String appId, String toUser, List<WxMaTemplateData> keywordValues,
                                      MaTemplateConfig templateConfig, String emphasisKeyword,
                                      String page, String formId) throws WxErrorException {
        WxMaTemplateMessage.WxMaTemplateMessageBuilder messageBuilder = null;
        WxMaMsgService service = mpAuthShopService.getMaServiceByShopId(getShopId()).getMsgService();
        WxMaTemplateService templateService = mpAuthShopService.getMaServiceByShopId(getShopId()).getTemplateService();
        String key = "MA_TEMPLATE_KEY_" + Util.md5(Util.toJson(templateConfig) + "_" + appId);
        Integer timeOut = 3600 * 24 * 365;
        String templateId = jedis.get(key);
        if (templateId == null) {
            templateId = this.getTemplate(templateService, templateConfig.getTitle(), templateConfig.getContent());
            if (templateId == null) {
                templateId = this.addTemplate(templateService, templateConfig.getId(), Arrays.asList(templateConfig.getKeywordIds()));
            }
            jedis.set(key, templateId, timeOut);
        }

        if (StringUtils.isNotBlank(page) ) {
            messageBuilder  = WxMaTemplateMessage.builder().toUser(toUser)
                .page(page)
                .data(keywordValues)
                .formId(formId)
                .emphasisKeyword(emphasisKeyword)
                .templateId(templateId);
        }
        try {
            service.sendTemplateMsg(messageBuilder.build());
        } catch (WxErrorException e) {
            // template_id不正确，移除缓存，重新发送模板消息
            int invalidTemplateIdCode = 40037;
            if (e.getError().getErrorCode() == invalidTemplateIdCode) {
                jedis.delete(key);
                sendMaTemplateMessage(appId, toUser, keywordValues, templateConfig, emphasisKeyword,
                    page, formId);
            }else {
                throw new WxErrorException(e.getError(), e);
            }
        }
        /**
         * TODO: 记录发送日志
         * 		shop($this->getShopId())->serviceRequest->messageRecord->addSendMessageRecord($toUser, $templateConfig['id'], 1, $page, $templateType, $mpLinkIdentity, $template_content,0);
         */
    }

    private String getTemplate(WxMaTemplateService service, String title, String content) throws WxErrorException {
        List<WxMaTemplateListResult.TemplateInfo> list = service.findTemplateList(0,20).getList();
        list.addAll(service.findTemplateList(0,20).getList());
        for (WxMaTemplateListResult.TemplateInfo info: list) {
            if (title.equals(info.getTitle())
                && content.equals(info.getContent().replaceAll("\\r|\\n| ", ""))) {
                return info.getTemplateId();
            }
        }
        return null;
    }
//	public String getTemplateId(String title,String content){
//	    mpAuthShopService.getMaServiceByShopId(getShopId())
//
//    }
}
