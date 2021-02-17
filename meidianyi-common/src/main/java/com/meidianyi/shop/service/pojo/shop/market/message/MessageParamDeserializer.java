package com.meidianyi.shop.service.pojo.shop.market.message;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateData;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubscribeMessageConfig;

import lombok.extern.slf4j.Slf4j;


/**
 * 消息专用反序列化
 * @author 卢光耀
 * @date 2019-08-26 16:22
 *
*/
@Slf4j
public class MessageParamDeserializer extends JsonDeserializer<RabbitMessageParam> {

    @Override
    public RabbitMessageParam deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        RabbitMessageParam param = RabbitMessageParam.builder().build();
        JsonNode node = p.getCodec().readTree(p);
        Iterator<String> iterator = node.fieldNames();
        while( iterator.hasNext() ){
            String key = iterator.next();
            JsonNode jNode = node;
            if( "shopId".equals(key) ){
                param.setShopId(jNode.findValue(key).asInt());
            }else if( "messageTemplateId".equals(key) ){
                param.setMessageTemplateId(jNode.findValue(key).asInt());
            }else if( "userIdList".equals(key) ){
                List<Integer> ids = new ArrayList<>();
                jNode.get(key).forEach(x->
                    ids.add(x.intValue()) );
                param.setUserIdList(ids);
            }else if( "page".equals(key )){
                param.setPage(jNode.findValue(key).asText());
            }else if( "type".equals(key) && !"null".equals(jNode.findValue(key).asText())){
                param.setType(jNode.findValue(key).asInt());
            }else if( "maTemplateData".equals(key) )  {
                JsonNode maData = jNode.findValue(key);
                if( maData.size()>0 ){
                	MaSubscribeData data = reSetMaData(maData);
                    MaTemplateData ma = MaTemplateData.builder()
                        .data(data)
                        .config(maData.findValue("config").textValue())
                        .build();
                    param.setMaTemplateData(ma);
                }
            }else if( "mpTemplateData".equals(key) )  {
                JsonNode mpData = jNode.findValue(key);
                if( mpData.size()>0 ) {
                    String[][] data = assemblyArray(mpData);
                    MpTemplateConfig config = MpTemplateConfig.getConfig(mpData.findPath("config").asText());
                    MpTemplateData mp = MpTemplateData.builder()
                        .data(data)
                        .config(config)
                        .build();
                    param.setMpTemplateData(mp);
                }
            }else if( "emphasisKeywordSn".equals(key) ){
                param.setEmphasisKeyword(jNode.findValue(key).asText());
            }else if("taskJobId".equals(key)){
                param.setTaskJobId(jNode.findValue(key).asInt());
            }
        }
        return param;
    }
    private String[][] assemblyArray(JsonNode mData){
        int size = mData.findValue("data").size();
        String[][] data = new String[size][3];
        for (int i = 0; i < size; i++) {
            JsonNode iNode = mData.findValue("data").get(i);
            for (int j = 0,jLen=iNode.size(); j < jLen ; j++) {
                data[i][j] = iNode.get(j).asText();
            }
        }
        return data;
    }
    private MaSubscribeData reSetMaData(JsonNode jsonNode) {
    	Set<Integer> secondIdList = SubscribeMessageConfig.getSecondIdList();
    	MaSubscribeData data=new MaSubscribeData();
    	Class<?> clazz = data.getClass();
    	for (Integer secondId : secondIdList) {
    		String fieldName="data"+secondId;
			JsonNode findValue = jsonNode.findValue(fieldName);
			Object array = assemblyMaArray(findValue);
			log.info("解析出的数据：{}",array);
			try {
				PropertyDescriptor pd = new PropertyDescriptor(fieldName, clazz);
				Method method = pd.getWriteMethod();
				method.invoke(data, array);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    	log.info("返回的data：{}",data.toString());
    	return data;
    }
    
    private String[][] assemblyMaArray(JsonNode mData){
        int size = mData.size();
        String[][] data = new String[size][1];
        for (int i = 0; i < size; i++) {
            JsonNode iNode = mData.get(i);
            for (int j = 0,jLen=iNode.size(); j < jLen ; j++) {
            	log.info("值:{}",iNode.get(j).asText());
                data[i][j] = iNode.get(j).asText();
            }
        }
        return data;
    }
}
