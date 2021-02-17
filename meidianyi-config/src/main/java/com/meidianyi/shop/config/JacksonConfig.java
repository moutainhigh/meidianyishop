package com.meidianyi.shop.config;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 *	
 * @author 李晓冰
 * @date 2019年7月9日
 */
@Configuration
public class JacksonConfig {

    @Value("${spring.jackson.date-format}")
	private String dateFormat;

    @Bean
    public SimpleModule customerTimestampModule(){
        Logger logger= LoggerFactory.getLogger(this.getClass());

        logger.debug("customer-timestamp-module begin init");
        SimpleModule simpleModule=new SimpleModule("customer-timestamp-module");
        simpleModule.addDeserializer(Timestamp.class,new CustomerTimestampDeserializer());
        simpleModule.addSerializer(Timestamp.class,new CustomerTimestampSerializer());
        logger.debug("customer-timestamp-module end init");
        return simpleModule;
    }

     class CustomerTimestampDeserializer extends JsonDeserializer<Timestamp> {

        @Override
        public Timestamp deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException{
            String text=p.getText();
            if(text!=null&&text.length()!=0){
                return Timestamp.valueOf(text);
            }
            return null;
        }
    }

    class CustomerTimestampSerializer extends JsonSerializer<Timestamp>{
        @Override
        public void serialize(Timestamp value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                String text = simpleDateFormat.format(value);
                gen.writeString(text);
            }
        }
    }
}
