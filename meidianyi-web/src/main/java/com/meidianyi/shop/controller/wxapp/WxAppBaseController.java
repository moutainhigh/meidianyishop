package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.service.shop.medicine.MedicalHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.meidianyi.shop.auth.WxAppAuth;
import com.meidianyi.shop.controller.BaseController;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.service.shop.ShopApplication;

/**
 * 
 * @author lixinguo
 *
 */
public class WxAppBaseController  extends BaseController {

	@Autowired
	protected WxAppAuth wxAppAuth;

    @Autowired
    protected MedicalHistoryService medicalHistoryService;
	
	/**
	 * 小程序前端语言，是由店铺控制的，需要在店铺读取 
	 */
	@Override
	protected String getLang() {
		ShopRecord record = saas.shop.getShopById(shopId());
		if(record==null) {
			throw new IllegalArgumentException("Invalid shopId");
		}
		return record.getShopLanguage();
	}
	
	/**
	 * 得到当前小程序店铺
	 * @return
	 */
	protected ShopApplication shop() {
		return saas.getShopApp(shopId());
	}
	
	/**
	 * 得到当前小程序ID
	 * @return
	 */
	protected Integer shopId() {
		return wxAppAuth.shopId();
	}
	
	
	/**
	 * 日志
	 * @return
	 */
	protected Logger logger() {
		return LoggerFactory.getLogger(this.getClass());
	}
}
