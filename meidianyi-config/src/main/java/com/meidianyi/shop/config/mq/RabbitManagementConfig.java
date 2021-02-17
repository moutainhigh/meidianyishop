package com.meidianyi.shop.config.mq;

import com.rabbitmq.http.client.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * rabbit可视化 management config
 * @author 卢光耀
 * @date 2019-09-18 14:09
 *
*/
@Configuration
public class RabbitManagementConfig {
    @Value("${rabbitmq.management.url}")
    private String url;

    @Value("${rabbitmq.username}")
    private String userName;

    @Value("${rabbitmq.password}")
    private String password;

    @Bean
    public Client client(){
        try {
            return new Client(url,userName,password);
        }  catch (MalformedURLException| URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
