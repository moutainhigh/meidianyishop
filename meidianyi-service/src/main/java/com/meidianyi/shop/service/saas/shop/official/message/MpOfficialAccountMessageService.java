package com.meidianyi.shop.service.saas.shop.official.message;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.wxapp.subscribe.MsgRecordParam;
import com.meidianyi.shop.service.saas.shop.official.MpOfficialAccountService;
import com.meidianyi.shop.service.pojo.shop.market.message.msgrecordconfig.MessageRecordType;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateIndustry;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;

/**
 * 
 * @author lixinguo
 *
 */
@Service
public class MpOfficialAccountMessageService extends MainBaseService {

	private static final int ERROR_CODE_INVALID_TEMPLATE_ID = 40037;
	protected static final String NEED_INDUSTRY_ID_1 = "1";
	protected static final String NEED_INDUSTRY_ID_2 = "2";
	protected static final String INDUSTRY_FIRST = "IT科技";
	protected static final String INDUSTRY_SECOND = "互联网/电子商务";



	@Autowired
	protected MpOfficialAccountService accountService;

	@Autowired
	protected JedisManager jedis;

	/**
	 * 设置行业
	 * 
	 * @param appId
	 * @throws WxErrorException
	 */
	public void setNeededIndustry(String appId) throws WxErrorException {
		WxMpTemplateMsgService service = accountService.getOfficialAccountClient(appId).getTemplateMsgService();
		WxMpTemplateIndustry originIndustry = service.getIndustry();
		if ( isNoSetIndustry(originIndustry) ) {
			return;
		}
		WxMpTemplateIndustry wxMpIndustry = new WxMpTemplateIndustry();
		wxMpIndustry.setPrimaryIndustry(new WxMpTemplateIndustry.Industry(NEED_INDUSTRY_ID_1));
		wxMpIndustry.setSecondIndustry(new WxMpTemplateIndustry.Industry(NEED_INDUSTRY_ID_2));
		service.setIndustry(wxMpIndustry);
	}

	private boolean isNoSetIndustry(WxMpTemplateIndustry originIndustry){
	    return INDUSTRY_FIRST.equals(originIndustry.getPrimaryIndustry().getFirstClass())
            && INDUSTRY_SECOND.equals(originIndustry.getPrimaryIndustry().getSecondClass())
            || INDUSTRY_FIRST.equals(originIndustry.getSecondIndustry().getFirstClass())
            && INDUSTRY_SECOND.equals(originIndustry.getSecondIndustry().getSecondClass());
    }

	/**
	 * 得到行业
	 * 
	 * @param appId
	 * @return
	 * @throws WxErrorException
	 */
	public WxMpTemplateIndustry getIndustry(String appId) throws WxErrorException {
		WxMpTemplateMsgService service = accountService.getOfficialAccountClient(appId).getTemplateMsgService();
		return service.getIndustry();
	}

	/**
	 * 发送模板消息
	 * 
	 * @param appId
	 * @param toUser
	 * @param keywordValues
	 * @param templateConfig
	 * @param maAppId        对应小程序AppId
	 * @param page           小程序路径
	 * @param url            网页路径（暂时无用默认设置为小程序的跳转路径）
	 */
	public void sendMpTemplateMessage(String appId, String toUser, List<WxMpTemplateData> keywordValues,
			MpTemplateConfig templateConfig, String maAppId,
			String page, String url,Integer shopId,Integer type,Integer userId,Integer messageTemplateId) throws WxErrorException {
		logger().info("发送模板消息");
        WxMpTemplateMessage.WxMpTemplateMessageBuilder messageBuilder = null;
		WxMpTemplateMsgService service = accountService.getOfficialAccountClient(appId).getTemplateMsgService();

		String key = "MP_TEMPLATE_KEY_" + Util.md5(Util.toJson(templateConfig) + "_" + appId);
		Integer timeOut = 3600 * 24 * 365;
		String templateId = jedis.get(key);
		if (templateId == null) {
			templateId = this.getTemplate(service, templateConfig.getTitle(), templateConfig.getContent());
			if (templateId == null) {
				templateId = this.addTemplate(service, templateConfig.getTemplateNo());
			}
			jedis.set(key, templateId, timeOut);
		}
        if( StringUtils.isNotBlank(url) ){
            messageBuilder  = WxMpTemplateMessage.builder().toUser(toUser).url(url)
                .templateId(templateId).data(keywordValues);
        }
		if (StringUtils.isNotBlank(page) && StringUtils.isNotBlank(maAppId)) {
            page = page + ((page.contains("?") ? "&" : "?") + "rnd=" + Util.randomId());
            messageBuilder.miniProgram(new WxMpTemplateMessage.MiniProgram(maAppId,page,false));
		}
		String sendTemplateMsg = null;
		try {
			sendTemplateMsg = service.sendTemplateMsg(messageBuilder.build());
		} catch (WxErrorException e) {
			// template_id不正确，移除缓存，重新发送模板消息
			if (e.getError().getErrorCode() == ERROR_CODE_INVALID_TEMPLATE_ID) {
				jedis.delete(key);
				sendMpTemplateMessage(appId, toUser, keywordValues, templateConfig, maAppId, page, url,shopId,type,userId,messageTemplateId);
			}else {
				throw new WxErrorException(e.getError(), e);
			}
		}
		recordMsg(userId, messageBuilder.build().toJson(), page, type, messageTemplateId.toString(), sendTemplateMsg, shopId, templateConfig.getTemplateNo());
	}

	/**
	 * 得到title content对应的模板
	 * 
	 * @param service
	 * @param title
	 * @param content
	 * @return
	 * @throws WxErrorException
	 */
	public String getTemplate(WxMpTemplateMsgService service, String title, String content) throws WxErrorException {
		List<WxMpTemplate> list = service.getAllPrivateTemplate();
		for (WxMpTemplate template : list) {
			if (title.equals(template.getTitle())
					&& content.equals(template.getContent().replaceAll("\\r|\\n| ", ""))) {
				return template.getTemplateId();
			}
		}
		return null;
	}

	/**
	 * 添加模板
	 * 
	 * @param service
	 * @param templateNo
	 * @return
	 * @throws WxErrorException
	 */
	public String addTemplate(WxMpTemplateMsgService service, String templateNo) throws WxErrorException {
		return service.addTemplate(templateNo);
	}
	
	/**
	 * 记录
	 * @param userId
	 * @param sendData
	 * @param page
	 * @param type
	 * @param templateId
	 * @param sendResult
	 * @param shopId
	 * @param templateNo
	 */
	public void recordMsg(Integer userId,String sendData,String page,Integer type,String templateId,String sendResult,Integer shopId,String templateNo) {
		logger().info("公众号消息记录");
		if(type.byteValue()!=MessageRecordType.TEMPLATE_TYPE_CUSTOM) {
			logger().info("type不是7，不记录");
			return;
		}
		MsgRecordParam param=new MsgRecordParam();
		param.setUserId(userId);
		param.setRequestAction(MessageRecordType.SYSTEM_SEND_MESSAGE);
		param.setIdentityId(templateNo);
		param.setTemplatePlatform((byte)2);
		param.setPage(page);
		param.setLinkIdentity(templateId);
		param.setTemplateContent(sendData);
		param.setResponseMsg(sendResult);
		param.setTemplateType(type.byteValue());
		logger().info("公众号消息记录准备发");
		saas.getShopApp(shopId).msgRecordService.addSendMessageRecord(param);
	}
}
