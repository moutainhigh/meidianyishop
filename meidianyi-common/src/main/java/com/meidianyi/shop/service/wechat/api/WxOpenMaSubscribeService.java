package com.meidianyi.shop.service.wechat.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;

import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.WxMaSubscribeMessage;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenGetResult;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMaSubScribeGeKeywordResult;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMaSubScribeGetCategoryResult;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMaSubScribeGetTemplateListResult;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMaSubScribeGetTemplateTitleResult;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMaSubscribeAddTemplateResult;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.result.WxOpenResult;

/**
 * 微信小程序订阅消息
 * 
 * @author lixinguo
 *
 */
public interface WxOpenMaSubscribeService extends WxOpenMaMpHttpBase {
	/**
	 * 组合模板并添加至帐号下的个人模板库
	 */
	static final String WX_SUBSCRIBE_ADD_TEMPLATE = "https://api.weixin.qq.com/wxaapi/newtmpl/addtemplate";
	/**
	 * 删除帐号下的个人模板
	 */
	static final String WX_SUBSCRIBE_DEL_TEMPLATE = "https://api.weixin.qq.com/wxaapi/newtmpl/deltemplate";

	/**
	 * 获取小程序账号的类目
	 */
	static final String WX_SUBSCRIBE_GET_CATEGORY = "https://api.weixin.qq.com/wxaapi/newtmpl/getcategory";

	/**
	 * 获取模板标题下的关键词列表
	 */
	static final String WX_SUBSCRIBE_GET_TEMPLATE_KEYWORDS = "https://api.weixin.qq.com/wxaapi/newtmpl/getpubtemplatekeywords";

	/**
	 * 获取帐号所属类目下的公共模板标题
	 */
	static final String WX_SUBSCRIBE_GET_TEMPLATE_TITLE = "https://api.weixin.qq.com/wxaapi/newtmpl/getpubtemplatetitles";

	/**
	 * 获取当前帐号下的个人模板列表
	 */
	static final String WX_SUBSCRIBE_GET_TEMPLATE_LIST = "https://api.weixin.qq.com/wxaapi/newtmpl/gettemplate";

	/**
	 * 发送订阅消息
	 */
	static final String WX_SUBSCRIBE_SEND = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send";

	/**
	 * 添加模板
	 * 
	 * @param appId
	 * @param tid
	 * @param kidList
	 * @param sceneDesc
	 * @return
	 * @throws WxErrorException
	 */
	default WxOpenMaSubscribeAddTemplateResult addTemplate(String appId, String tid, int[] kidList, String sceneDesc)
			throws WxErrorException {
		StringBuilder data = new StringBuilder();
		try {
			data.append("tid=" + URLEncoder.encode(tid, "UTF-8"));
			for (int i = 0; i < kidList.length; i++) {
				String key = URLEncoder.encode("kidList[" + i + "]", "UTF-8");
				data.append("&" + key + "=" + URLEncoder.encode(String.valueOf(kidList[i]), "UTF-8"));
			}
			data.append("&sceneDesc=" + URLEncoder.encode(sceneDesc, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String json = postForm(appId, WX_SUBSCRIBE_ADD_TEMPLATE, data.toString());
		return WxOpenMaSubscribeAddTemplateResult.fromJson(json);
	}

	/**
	 * 删除模板
	 * 
	 * @param appId
	 * @param priTmplId
	 * @return
	 * @throws WxErrorException
	 */
	default WxOpenResult delTemplate(String appId, String priTmplId) throws WxErrorException {
		StringBuilder data = new StringBuilder();
		try {
			data.append("priTmplId=" + URLEncoder.encode(priTmplId, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String json = this.postForm(appId, WX_SUBSCRIBE_DEL_TEMPLATE, data.toString());
		return WxOpenGetResult.fromJson(json);
	}

	/**
	 * 获取小程序账号的类目
	 * 
	 * @param appId
	 * @return
	 * @throws WxErrorException
	 */
	default WxOpenMaSubScribeGetCategoryResult getTemplateCategory(String appId) throws WxErrorException {
		String json = get(appId, WX_SUBSCRIBE_GET_CATEGORY, StringUtils.EMPTY);
		return WxOpenMaSubScribeGetCategoryResult.fromJson(json);
	}

	/**
	 * 获取模板标题下的关键词列表
	 * 
	 * @param appId
	 * @param tid   模板Id
	 * @return
	 * @throws WxErrorException
	 */
	default WxOpenMaSubScribeGeKeywordResult getPubTemplateKeyWordsById(String appId, String tid)
			throws WxErrorException {
		StringBuilder data = new StringBuilder();
		try {
			data.append("tid=" + URLEncoder.encode(tid, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String json = get(appId, WX_SUBSCRIBE_GET_TEMPLATE_KEYWORDS, data.toString());
		return WxOpenMaSubScribeGeKeywordResult.fromJson(json);
	}

	/**
	 * 获取帐号所属类目下的公共模板标题
	 * 
	 * @param appId
	 * @param ids
	 * @param start
	 * @param limit
	 * @return
	 * @throws WxErrorException
	 */
	default WxOpenMaSubScribeGetTemplateTitleResult getPubTemplateTitleList(String appId, String ids, int start,
			int limit) throws WxErrorException {
		StringBuilder data = new StringBuilder();
		try {
			data.append("ids=" + URLEncoder.encode(ids, "UTF-8"));
			data.append("&start=" + URLEncoder.encode(String.valueOf(start), "UTF-8"));
			data.append("&limit=" + URLEncoder.encode(String.valueOf(limit), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String json = get(appId, WX_SUBSCRIBE_GET_TEMPLATE_TITLE, data.toString());
		return WxOpenMaSubScribeGetTemplateTitleResult.fromJson(json);
	}

	/**
	 * 获取当前帐号下的个人模板列表
	 * 
	 * @param appId
	 * @return
	 * @throws WxErrorException
	 */
	default WxOpenMaSubScribeGetTemplateListResult getTemplateList(String appId) throws WxErrorException {
		String json = get(appId, WX_SUBSCRIBE_GET_TEMPLATE_LIST, StringUtils.EMPTY);
		return WxOpenMaSubScribeGetTemplateListResult.fromJson(json);
	}

	/**
	 * 发送订阅消息
	 * 
	 * @param appId
	 * @param param   参数
     *
	 * @return
	 * @throws WxErrorException
	 */
	default WxOpenResult sendTemplate(String appId, WxMaSubscribeMessage param) throws WxErrorException {
		String json = post(appId, WX_SUBSCRIBE_SEND, param.toJson());
		return WxOpenGetResult.fromJson(json);
	}
}
