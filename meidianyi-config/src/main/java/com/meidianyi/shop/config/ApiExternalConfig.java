package com.meidianyi.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author 李晓冰
 * @date 2020年07月15日
 */
@Configuration
public class ApiExternalConfig {
    @Value(value = "${api.external.service.sign.key}")
    private String signKey;

    public String getSignKey() {
        return signKey;
    }
}
