package com.meidianyi.shop.service.shop.member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.shop.config.BaseShopConfigService;
/**
 * 	用户导出配置
 * @author 黄壮壮
 *
 */
@Service
public class UserExpCfgService extends BaseShopConfigService {
	@Autowired
	private UserExportService uExpSvc;
	/**
	 * 	导出会员配置key
	 */
	private static final String KEY = "userExport_list";
	
	/**
	 * 	将会员配置信息以List Json形式存储
	 * @param value
	 */
	public void setUserExpCfg(List<String> value) {
		String strValue = Util.toJson(value);
		if(strValue==null) {
			return;
		}
		set(KEY,strValue,String.class);
	}
	
	public List<String> getUserExpCfg() {
		String res = get(KEY,String.class,null);
		if(StringUtils.isBlank(res)) {
			return new ArrayList<String>(Arrays.asList(uExpSvc.getFirstColName()));
		}
		
		return Util.json2Object(res, new TypeReference<List<String>>() {
        }, false);
	}
}
