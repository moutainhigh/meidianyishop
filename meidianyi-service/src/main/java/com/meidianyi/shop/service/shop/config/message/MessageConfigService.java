package com.meidianyi.shop.service.shop.config.message;

import com.meidianyi.shop.dao.shop.config.MessageTemplateConfigDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.message.MessageConfigParam;
import com.meidianyi.shop.service.pojo.shop.config.message.MessageConfigVo;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitParamConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lixinguo
 */
@Service
public class MessageConfigService extends ShopBaseService {

	@Autowired
	MessageTemplateConfigDao messageTemplateConfigDao;

    public void updateMessageConfig(MessageConfigParam param){
    	messageTemplateConfigDao.batchUpdate(param.getConfigs());
    }

    public List<MessageConfigVo> getAllMessageConfig(){
       return  messageTemplateConfigDao.getAllMessageConfig();
    }

    public MessageConfigVo getMessageConfig(Integer id){
        return  messageTemplateConfigDao.getMessageConfig(id);
    }

    /**
     * 校验是否配置开通发送对因类型的消息
     * @param type 消息类型
     * @return true 开通
     */
    public boolean checkConfig(Integer type){
    	if(type.equals(RabbitParamConstant.Type.MP_TEMPLE_TYP_NO)||type.equals(RabbitParamConstant.Type.MP_TEMPLE_TYPE_NO_USER)) {
    		return true;
    	}
        return messageTemplateConfigDao.hasConfig(type);
    }
}
