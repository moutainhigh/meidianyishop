package com.meidianyi.shop.service.shop.user.message;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.db.shop.tables.records.ServiceMessageRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.wxapp.subscribe.MsgRecordParam;
import com.meidianyi.shop.service.shop.user.user.UserService;

import static com.meidianyi.shop.db.shop.tables.ServiceMessageRecord.SERVICE_MESSAGE_RECORD;

/**
 * 消息记录
 * 
 * @author zhaojianqiang
 * @time 上午9:11:38
 */
@Service
public class MessageRecordService extends ShopBaseService {
	@Autowired
	private UserService userService;

	/**
	 * 生成模板消息发送记录
	 * 
	 * @param param
	 */
	public void addSendMessageRecord(MsgRecordParam param) {
		logger().info("进入生成模板消息发送记录");
		UserRecord user = userService.getUserByUserId(param.getUserId());
		ServiceMessageRecordRecord data = db().newRecord(SERVICE_MESSAGE_RECORD, param);
		String mobile = user.getMobile();
		if (StringUtils.isNotEmpty(mobile)) {
			data.setMobile(mobile);
		}
		String page = param.getPage();
		if (StringUtils.isNoneEmpty(page)) {
			int indexOf = page.indexOf("?");
			if (indexOf != -1) {
				String path = page.substring(0, indexOf);
				String pathQuery = page.substring(indexOf + 1, page.length());
				data.setPath(path);
				data.setPathQuery(pathQuery);
			}
		} else {
			data.setPath("pages/index/index");
		}
		int insert = data.insert();
		logger().info("生成模板消息;发送记录:" + insert);

	}

}
