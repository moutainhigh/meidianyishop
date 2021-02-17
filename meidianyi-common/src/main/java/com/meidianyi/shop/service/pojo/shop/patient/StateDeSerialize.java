package com.meidianyi.shop.service.pojo.shop.patient;



import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;


/**
 * @author yangpengcheng
 */
public class StateDeSerialize extends JsonDeserializer<Byte> {

    @Override
    public Byte deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        boolean isDisableOrDelete = jsonParser != null && (BaseConstant.EXTERNAL_ITEM_STATE_DELETE.equals(Integer.parseInt(jsonParser.getText()))
            || BaseConstant.EXTERNAL_ITEM_STATE_DISABLE.equals(Integer.parseInt(jsonParser.getText())));
        if (isDisableOrDelete) {
            return (byte) 1;
        }else if (jsonParser != null && (BaseConstant.EXTERNAL_ITEM_STATE_ENABLE.equals(Integer.parseInt(jsonParser.getText())))) {
            return (byte) 0;
        }
        return (byte)Integer.parseInt(jsonParser.getText());
    }
}
