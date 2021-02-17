package com.meidianyi.shop.controller.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.meidianyi.shop.auth.SystemAuth;
import com.meidianyi.shop.controller.BaseController;
/**
 * 
 * @author 新国
 *
 */
public class SystemBaseController extends BaseController {

	@Autowired
	protected SystemAuth sysAuth;
	

	/**
	 * 日志
	 * @return
	 */
	protected Logger logger() {
		return LoggerFactory.getLogger(this.getClass());
	}
}
