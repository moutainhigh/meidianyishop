package com.meidianyi.shop.dao.shop.config;

import static com.meidianyi.shop.db.shop.tables.MessageTemplateConfig.MESSAGE_TEMPLATE_CONFIG;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.Batch;

import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.service.pojo.shop.config.message.Config;
import com.meidianyi.shop.service.pojo.shop.config.message.MessageConfigVo;
import org.springframework.stereotype.Repository;

@Repository
public class MessageTemplateConfigDao extends ShopBaseDao {

	/**
	 * 批量更新添加配置消息配置模板
	 *
	 * @param configs
	 */
	public void batchUpdate(List<Config> configs) {
		Batch batch = db().batchUpdate(
				configs.stream().map(x -> db().newRecord(MESSAGE_TEMPLATE_CONFIG, x)).collect(Collectors.toList()));
		batch.execute();
	}

	/**
	 * 得到所有消息模板配置
	 *
	 * @return
	 */
	public List<MessageConfigVo> getAllMessageConfig() {
		return db().selectFrom(MESSAGE_TEMPLATE_CONFIG).fetch().into(MessageConfigVo.class);
	}

	/**
	 * 得到id对应消息模板配置
	 *
	 * @param id
	 * @return
	 */
	public MessageConfigVo getMessageConfig(Integer id) {
		return db().selectFrom(MESSAGE_TEMPLATE_CONFIG).where(MESSAGE_TEMPLATE_CONFIG.ID.eq(id))
				.fetchOneInto(MessageConfigVo.class);
	}

	/**
	 * 校验是否配置开通发送对因类型的消息
	 *
	 * @param type 消息类型
	 * @return true 开通
	 */
	public boolean hasConfig(Integer type) {
		Byte openMp = db().select(MESSAGE_TEMPLATE_CONFIG.OPEN_MP).from(MESSAGE_TEMPLATE_CONFIG)
				.where(MESSAGE_TEMPLATE_CONFIG.ID.eq(type)).fetchOne(MESSAGE_TEMPLATE_CONFIG.OPEN_MP);
		return openMp != null && openMp == 1;
	}
}
