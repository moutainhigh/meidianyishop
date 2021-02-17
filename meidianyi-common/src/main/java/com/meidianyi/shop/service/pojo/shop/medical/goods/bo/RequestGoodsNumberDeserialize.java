package com.meidianyi.shop.service.pojo.shop.medical.goods.bo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.meidianyi.shop.service.pojo.shop.medical.goods.MedicalGoodsConstant;

import java.io.IOException;

/**
 * @author 李晓冰
 * @date 2020年09月09日
 */
public class RequestGoodsNumberDeserialize extends JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        try {
            String text = p.getText();
            Double aDouble = Double.valueOf(text);
            return aDouble.intValue();
        } catch (Exception e) {
            return MedicalGoodsConstant.MEDICAL_GOODS_DEFAULT_NUM;
        }
    }
}
