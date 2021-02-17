package com.meidianyi.shop.service.pojo.shop.market.message.jsonbuilder;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.WxMaSubscribeMessage;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.WxMaSubscribeMessageData;

/**
 * @author zhaojianqiang
 */
public class WxMaSubscribeMessageGsonAdapter implements JsonSerializer<WxMaSubscribeMessage>{

	@Override
	public JsonElement serialize(WxMaSubscribeMessage message, Type typeOfSrc, JsonSerializationContext context) {
	    JsonObject messageJson = new JsonObject();
	    messageJson.addProperty("touser", message.getTouser());
	    messageJson.addProperty("template_id", message.getTemplateId());
	    if (message.getPage() != null) {
	      messageJson.addProperty("page", message.getPage());
	    }

	    JsonObject data = new JsonObject();
	    messageJson.add("data", data);

	    for (WxMaSubscribeMessageData datum : message.getData()) {
	      JsonObject dataJson = new JsonObject();
	      dataJson.addProperty("value", datum.getValue());
	      data.add(datum.getName(), dataJson);
	    }
	    return messageJson;
	}

}
