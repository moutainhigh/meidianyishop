package com.meidianyi.shop.service.foundation.util;

import org.apache.commons.lang3.StringUtils;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;

/**
 * 备注国际化工具
 * @author 黄壮壮
 * 
 */
public class RemarkUtil {
	
	final static String LANGUAGE_TYPE="remark";
	/**
	 * 处理备注国际化
	 * @param language 语言
	 * @param tmpId	模板Id
	 * @param tmpData 模板数据
	 * @return String remark 国际化的备注
	 */
	public static String remarkI18N(String language,Integer tmpId,String tmpData) {
		// remark i18n
		String remark;
		String msgTmp = RemarkTemplate.getMessageByCode(tmpId);
		if(!StringUtils.isBlank(msgTmp)) {
			// 系统定义信息
			if(StringUtils.isBlank(tmpData)) {
				// 直接翻译
				remark = Util.translateMessage(language, msgTmp, LANGUAGE_TYPE);
			}else {
				// 需要数据进行翻译
				remark = Util.translateMessage(language, msgTmp, LANGUAGE_TYPE, (Object[])tmpData.split(","));
			}
		}else {
			// 来自用户输入的数据，不进行翻译
			remark = tmpData;
		}
		return remark;
	}
}
